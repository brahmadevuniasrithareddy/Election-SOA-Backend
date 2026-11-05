const express = require("express");
const cors = require("cors");
const sqlite3 = require("sqlite3").verbose();
const bcrypt = require("bcryptjs");

const app = express();
const PORT = 5000;

// Middleware
app.use(cors());
app.use(express.json());

// Database
const db = new sqlite3.Database("./election.db", (err) => {
    if (err) {
        console.error("Database connection failed:", err.message);
    } else {
        console.log("Connected to SQLite database.");
    }
});

// Create tables
db.serialize(() => {

    db.run(`
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            has_voted INTEGER DEFAULT 0
        )
    `);

    db.run(`
        CREATE TABLE IF NOT EXISTS candidates (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            party TEXT NOT NULL
        )
    `);

    db.run(`
        CREATE TABLE IF NOT EXISTS votes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            candidate_id INTEGER NOT NULL,
            voted_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    `);

    db.run(`
        CREATE TABLE IF NOT EXISTS audit_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            action TEXT NOT NULL,
            details TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    `);

    // Demo candidates
    db.get("SELECT COUNT(*) AS count FROM candidates", (err, row) => {
        if (err) {
            console.error(err);
            return;
        }

        if (row.count === 0) {
            const stmt = db.prepare(
                "INSERT INTO candidates (name, party) VALUES (?, ?)"
            );

            stmt.run("Candidate A", "Party A");
            stmt.run("Candidate B", "Party B");
            stmt.run("Candidate C", "Party C");

            stmt.finalize();

            console.log("Demo candidates created.");
        }
    });

    // Demo voter
    db.get(
        "SELECT * FROM users WHERE email = ?",
        ["voter@example.com"],
        async (err, user) => {

            if (err) {
                console.error(err);
                return;
            }

            if (!user) {
                const hashedPassword = await bcrypt.hash(
                    "password123",
                    10
                );

                db.run(
                    `INSERT INTO users (name, email, password)
                     VALUES (?, ?, ?)`,
                    [
                        "Demo Voter",
                        "voter@example.com",
                        hashedPassword
                    ],
                    (err) => {
                        if (err) {
                            console.error(
                                "Demo user creation failed:",
                                err.message
                            );
                        } else {
                            console.log(
                                "Demo voter created."
                            );
                        }
                    }
                );
            }
        }
    );
});

// Test route
app.get("/", (req, res) => {
    res.json({
        message: "Digital Election Server is running"
    });
});

// Signup
app.post("/api/signup", async (req, res) => {

    const { name, email, password } = req.body;

    if (!name || !email || !password) {
        return res.status(400).json({
            message: "All fields are required."
        });
    }

    try {

        const hashedPassword = await bcrypt.hash(password, 10);

        db.run(
            `INSERT INTO users (name, email, password)
             VALUES (?, ?, ?)`,
            [name, email, hashedPassword],
            function (err) {

                if (err) {

                    if (err.message.includes("UNIQUE")) {
                        return res.status(409).json({
                            message: "Email already registered."
                        });
                    }

                    return res.status(500).json({
                        message: "Could not create account."
                    });
                }

                db.run(
                    `INSERT INTO audit_logs
                     (user_id, action, details)
                     VALUES (?, ?, ?)`,
                    [
                        this.lastID,
                        "ACCOUNT_CREATED",
                        `Account created for ${email}`
                    ]
                );

                res.status(201).json({
                    message: "Account created successfully."
                });
            }
        );

    } catch (error) {

        res.status(500).json({
            message: "Server error."
        });
    }
});

// Login
app.post("/api/login", (req, res) => {

    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({
            message: "Email and password are required."
        });
    }

    db.get(
        "SELECT * FROM users WHERE email = ?",
        [email],
        async (err, user) => {

            if (err) {
                console.error(err);

                return res.status(500).json({
                    message: "Database error."
                });
            }

            if (!user) {
                return res.status(401).json({
                    message: "Invalid email or password."
                });
            }

            const passwordMatch = await bcrypt.compare(
                password,
                user.password
            );

            if (!passwordMatch) {
                return res.status(401).json({
                    message: "Invalid email or password."
                });
            }

            db.run(
                `INSERT INTO audit_logs
                 (user_id, action, details)
                 VALUES (?, ?, ?)`,
                [
                    user.id,
                    "LOGIN",
                    `User ${user.email} logged in`
                ]
            );

            res.json({
                message: "Login successful.",
                user: {
                    id: user.id,
                    name: user.name,
                    email: user.email,
                    hasVoted: user.has_voted === 1
                }
            });
        }
    );
});

// Get candidates
app.get("/api/candidates", (req, res) => {

    db.all(
        "SELECT * FROM candidates",
        [],
        (err, candidates) => {

            if (err) {
                return res.status(500).json({
                    message: "Could not load candidates."
                });
            }

            res.json(candidates);
        }
    );
});

// Cast vote
app.post("/api/vote", (req, res) => {

    const { userId, candidateId } = req.body;

    if (!userId || !candidateId) {
        return res.status(400).json({
            message: "User and candidate are required."
        });
    }

    db.get(
        "SELECT * FROM users WHERE id = ?",
        [userId],
        (err, user) => {

            if (err || !user) {
                return res.status(404).json({
                    message: "User not found."
                });
            }

            if (user.has_voted === 1) {
                return res.status(403).json({
                    message: "You have already voted."
                });
            }

            db.get(
                "SELECT * FROM candidates WHERE id = ?",
                [candidateId],
                (err, candidate) => {

                    if (err || !candidate) {
                        return res.status(404).json({
                            message: "Candidate not found."
                        });
                    }

                    db.run(
                        `INSERT INTO votes
                         (user_id, candidate_id)
                         VALUES (?, ?)`,
                        [userId, candidateId],
                        function (err) {

                            if (err) {
                                return res.status(500).json({
                                    message: "Vote could not be recorded."
                                });
                            }

                            db.run(
                                `UPDATE users
                                 SET has_voted = 1
                                 WHERE id = ?`,
                                [userId]
                            );

                            db.run(
                                `INSERT INTO audit_logs
                                 (user_id, action, details)
                                 VALUES (?, ?, ?)`,
                                [
                                    userId,
                                    "VOTE_CAST",
                                    `Vote cast for candidate ${candidate.name}`
                                ]
                            );

                            res.json({
                                message: "Vote submitted successfully."
                            });
                        }
                    );
                }
            );
        }
    );
});

// Start server
app.listen(PORT, () => {

    console.log("----------------------------------");
    console.log("Digital Election Server");
    console.log("----------------------------------");
    console.log(`Server running at http://localhost:${PORT}`);
    console.log("----------------------------------");

});