import { useState } from "react";

const PRIMARY_API_BASE = process.env.REACT_APP_API_BASE_URL ||
  (window.location.hostname === "localhost" ? "http://localhost:8080" : "");

export default function App() {
  const [clientId, setClientId] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [employment, setEmployment] = useState("");
  const [result, setResult] = useState("Ready.");
  const [loading, setLoading] = useState(false);

  const doRequest = async (path, options = {}) => {
    setLoading(true);
    try {
      const requestOptions = { ...options };
      if (requestOptions.body) {
        requestOptions.headers = {
          "Content-Type": "application/json",
          ...(requestOptions.headers || {}),
        };
      }

      let response = await fetch(`${PRIMARY_API_BASE}${path}`, {
        ...requestOptions,
      });

      const contentType = response.headers.get("content-type") || "";
      const hasJson = contentType.includes("application/json");
      const payload = hasJson ? await response.json() : await response.text();

      if (!response.ok) {
        throw new Error(
          typeof payload === "string" && payload.trim()
            ? payload
            : `Request failed (${response.status})`
        );
      }

      setResult(
        payload ? JSON.stringify(payload, null, 2) : `${response.status} ${response.statusText}`
      );
    } catch (error) {
      setResult(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleView = () => {
    if (clientId.trim()) {
      doRequest(`/api/clients/${clientId.trim()}`, { method: "GET" });
      return;
    }
    doRequest("/api/clients", { method: "GET" });
  };

  const handleCreate = () => {
    doRequest("/api/clients", {
      method: "POST",
      body: JSON.stringify({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        employment: employment.trim(),
      }),
    });
  };

  const handleModify = () => {
    if (!clientId.trim()) {
      setResult("Error: clientId is required for MODIFY.");
      return;
    }
    doRequest(`/api/clients/${clientId.trim()}`, {
      method: "PUT",
      body: JSON.stringify({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        employment: employment.trim(),
      }),
    });
  };

  const handleDelete = () => {
    if (!clientId.trim()) {
      setResult("Error: clientId is required for DELETE.");
      return;
    }
    doRequest(`/api/clients/${clientId.trim()}`, { method: "DELETE" });
  };

  const handleClear = () => {
    setClientId("");
    setFirstName("");
    setLastName("");
    setEmployment("");
  };

  return (
    <main className="page">
      <p style={{ color: "red", margin: "0 0 8px", fontSize: "0.9rem" }}>
        Since this is a free Render project, it can take up to 50 seconds to run the backend from idle.
      </p>
      <h1>Client CRUD</h1>

      <label htmlFor="clientId">Client ID (for single VIEW enter number otherwise leave bank for all, MODIFY, DELETE)</label>
      <input
        id="clientId"
        type="number"
        value={clientId}
        onChange={(e) => setClientId(e.target.value)}
        placeholder="e.g. 1"
      />

      <label htmlFor="firstName">First Name (CREATE/MODIFY)</label>
      <input
        id="firstName"
        type="text"
        value={firstName}
        onChange={(e) => setFirstName(e.target.value)}
        placeholder="e.g. Jane"
      />

      <label htmlFor="lastName">Last Name (CREATE/MODIFY)</label>
      <input
        id="lastName"
        type="text"
        value={lastName}
        onChange={(e) => setLastName(e.target.value)}
        placeholder="e.g. Doe"
      />

      <label htmlFor="employment">Employment (CREATE/MODIFY)</label>
      <input
        id="employment"
        type="text"
        value={employment}
        onChange={(e) => setEmployment(e.target.value)}
        placeholder="e.g. W2"
      />

      <div className="buttons">
        <button onClick={handleView} disabled={loading}>VIEW</button>
        <button onClick={handleCreate} disabled={loading}>CREATE</button>
        <button onClick={handleModify} disabled={loading}>MODIFY</button>
        <button onClick={handleDelete} disabled={loading}>DELETE</button>
        <button onClick={handleClear} disabled={loading}>Clear</button>
      </div>

      <h2>Response</h2>
      <pre>{loading ? "Working..." : result}</pre>
    </main>
  );
}
