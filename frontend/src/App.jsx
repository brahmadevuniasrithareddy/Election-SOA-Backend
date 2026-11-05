import { useState } from "react";
import "./App.css";
import axios from "axios";

const API = "http://localhost:8080";

function App() {
  const [page, setPage] = useState("login");
  const [message, setMessage] = useState("");

  const [loginData, setLoginData] = useState({
    username: "",
    password: "",
  });

  const [signupData, setSignupData] = useState({
    fullName: "",
    username: "",
    email: "",
    password: "",
  });

  // ==============================
  // SIGNUP
  // ==============================

  const handleSignup = async (event) => {
    event.preventDefault();
    setMessage("Creating account...");

    try {
      const response = await axios.post(
        `${API}/api/auth/signup`,
        signupData
      );

      console.log("Signup response:", response.data);

      setMessage(
        "Account created successfully! Please login."
      );

      setSignupData({
        fullName: "",
        username: "",
        email: "",
        password: "",
      });

      setTimeout(() => {
        setPage("login");
        setMessage("");
      }, 1500);

    } catch (error) {
      console.error("Signup error:", error);

      if (error.response) {
        console.log("Status:", error.response.status);
        console.log("Response:", error.response.data);

        let errorMessage = "Signup failed";

        if (typeof error.response.data === "string") {
          errorMessage = error.response.data;
        } else if (error.response.data?.message) {
          errorMessage = error.response.data.message;
        }

        setMessage(errorMessage);

      } else if (error.request) {

        setMessage(
          "Cannot connect to the election server. Make sure API Gateway is running on port 8080."
        );

      } else {

        setMessage(
          "An unexpected error occurred."
        );
      }
    }
  };

  // ==============================
  // LOGIN
  // ==============================

  const handleLogin = async (event) => {
    event.preventDefault();
    setMessage("Logging in...");

    try {
      const response = await axios.post(
        `${API}/api/auth/login`,
        loginData
      );

      console.log("Login response:", response.data);

      localStorage.setItem(
        "token",
        response.data.token
      );

      setMessage("Login successful!");

      setTimeout(() => {
        setPage("dashboard");
        setMessage("");
      }, 1000);

    } catch (error) {
      console.error("Login error:", error);

      if (error.response) {

        if (typeof error.response.data === "string") {
          setMessage(error.response.data);
        } else if (error.response.data?.message) {
          setMessage(error.response.data.message);
        } else {
          setMessage("Invalid username or password.");
        }

      } else if (error.request) {

        setMessage(
          "Cannot connect to the election server."
        );

      } else {

        setMessage(
          "An unexpected error occurred."
        );
      }
    }
  };

  // ==============================
  // LOGOUT
  // ==============================

  const handleLogout = () => {
    localStorage.removeItem("token");

    setLoginData({
      username: "",
      password: "",
    });

    setMessage("");

    setPage("login");
  };

  // ==============================
  // SIGNUP PAGE
  // ==============================

  if (page === "signup") {
    return (
      <div className="app">

        <div className="card">

          <h1>Digital Election System</h1>

          <p className="subtitle">
            Tamper-Resistant Digital Election & Vote Auditing
          </p>

          <h2>Create Voter Account</h2>

          <form onSubmit={handleSignup}>

            <input
              type="text"
              placeholder="Full Name"
              value={signupData.fullName}
              onChange={(event) =>
                setSignupData({
                  ...signupData,
                  fullName: event.target.value,
                })
              }
              required
            />

            <input
              type="text"
              placeholder="Username"
              value={signupData.username}
              onChange={(event) =>
                setSignupData({
                  ...signupData,
                  username: event.target.value,
                })
              }
              required
            />

            <input
              type="email"
              placeholder="Email"
              value={signupData.email}
              onChange={(event) =>
                setSignupData({
                  ...signupData,
                  email: event.target.value,
                })
              }
              required
            />

            <input
              type="password"
              placeholder="Password"
              value={signupData.password}
              onChange={(event) =>
                setSignupData({
                  ...signupData,
                  password: event.target.value,
                })
              }
              required
            />

            <button type="submit">
              Create Account
            </button>

          </form>

          {message && (
            <div className="message">
              {message}
            </div>
          )}

          <button
            className="secondary"
            onClick={() => {
              setPage("login");
              setMessage("");
            }}
          >
            Back to Login
          </button>

        </div>

      </div>
    );
  }

  // ==============================
  // DASHBOARD
  // ==============================

  if (page === "dashboard") {
    return (
      <div className="app">

        <div className="dashboard">

          <div className="header">

            <div>
              <h1>Digital Election System</h1>

              <p>
                Voter Dashboard
              </p>
            </div>

            <button
              className="logout"
              onClick={handleLogout}
            >
              Logout
            </button>

          </div>

          <div className="welcome">

            <h2>
              Welcome to the Election System
            </h2>

            <p>
              You have successfully logged in.
            </p>

            <p>
              Your election information will appear here.
            </p>

          </div>

        </div>

      </div>
    );
  }

  // ==============================
  // LOGIN PAGE
  // ==============================

  return (
    <div className="app">

      <div className="card">

        <h1>
          Digital Election System
        </h1>

        <p className="subtitle">
          Tamper-Resistant Digital Election & Vote Auditing
        </p>

        <h2>
          Voter Login
        </h2>

        <form onSubmit={handleLogin}>

          <input
            type="text"
            placeholder="Username"
            value={loginData.username}
            onChange={(event) =>
              setLoginData({
                ...loginData,
                username: event.target.value,
              })
            }
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={loginData.password}
            onChange={(event) =>
              setLoginData({
                ...loginData,
                password: event.target.value,
              })
            }
            required
          />

          <button type="submit">
            Login
          </button>

        </form>

        {message && (
          <div className="message">
            {message}
          </div>
        )}

        <p className="account-text">
          Don't have an account?
        </p>

        <button
          className="secondary"
          onClick={() => {
            setPage("signup");
            setMessage("");
          }}
        >
          Create Account
        </button>

      </div>

    </div>
  );
}

export default App;