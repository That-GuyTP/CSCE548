import { useEffect, useState } from "react";

const API_BASE = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080";

export default function App() {
  const [status, setStatus] = useState("Loading...");

  useEffect(() => {
    fetch(`${API_BASE}/api/health`)
      .then((r) => r.text())
      .then(setStatus)
      .catch(() => setStatus("Failed to reach backend"));
  }, []);

  return (
    <div style={{ fontFamily: "Arial", padding: 24 }}>
      <h1>Client Information Manager</h1>
      <p><b>Backend status:</b> {status}</p>
    </div>
  );
}