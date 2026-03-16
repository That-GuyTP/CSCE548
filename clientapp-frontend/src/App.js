import { useState } from "react";

const PRIMARY_API_BASE = process.env.REACT_APP_API_BASE_URL ||
  (window.location.hostname === "localhost" ? "http://localhost:8080" : "");

const isClientPayload = (value) =>
  !!value &&
  typeof value === "object" &&
  !Array.isArray(value) &&
  ("clientId" in value || ("firstName" in value && "lastName" in value && "employment" in value));

const formatField = (value) => {
  if (value === null || value === undefined) return "Not provided";
  if (typeof value === "string" && value.trim() === "") return "Not provided";
  return value;
};

const formatSalary = (value) => {
  const parsed = Number(value);
  if (Number.isNaN(parsed)) return "Not provided";
  return parsed.toLocaleString("en-US", { style: "currency", currency: "USD" });
};

const normalizeDateOfBirth = (value) => {
  const trimmed = value.trim();
  if (!trimmed) return null;

  let normalized = trimmed;
  const slashDate = trimmed.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
  if (slashDate) {
    const [, mm, dd, yyyy] = slashDate;
    normalized = `${yyyy}-${mm.padStart(2, "0")}-${dd.padStart(2, "0")}`;
  }

  if (!/^\d{4}-\d{2}-\d{2}$/.test(normalized)) return null;

  const [yearText, monthText, dayText] = normalized.split("-");
  const year = Number(yearText);
  const month = Number(monthText);
  const day = Number(dayText);
  const parsedDate = new Date(Date.UTC(year, month - 1, day));
  const isValidDate =
    parsedDate.getUTCFullYear() === year &&
    parsedDate.getUTCMonth() === month - 1 &&
    parsedDate.getUTCDate() === day;

  return isValidDate ? normalized : null;
};

export default function App() {
  const [clientId, setClientId] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [employment, setEmployment] = useState("");
  const [personFirstName, setPersonFirstName] = useState("");
  const [personLastName, setPersonLastName] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [address, setAddress] = useState("");
  const [legalSex, setLegalSex] = useState("");
  const [businessName, setBusinessName] = useState("");
  const [positionName, setPositionName] = useState("");
  const [salary, setSalary] = useState("");
  const [status, setStatus] = useState("Ready.");
  const [resultData, setResultData] = useState(null);
  const [loading, setLoading] = useState(false);

  const sendRequest = async (path, options = {}) => {
    const requestOptions = { ...options };
    if (requestOptions.body) {
      requestOptions.headers = {
        "Content-Type": "application/json",
        ...(requestOptions.headers || {}),
      };
    }

    const response = await fetch(`${PRIMARY_API_BASE}${path}`, {
      ...requestOptions,
    });

    const contentType = response.headers.get("content-type") || "";
    const hasJson = contentType.includes("application/json");
    const payload = hasJson ? await response.json() : await response.text();

    if (!response.ok) {
      const message = typeof payload === "string"
        ? payload
        : payload?.message || payload?.error || "";
      throw new Error(message.trim() || `Request failed (${response.status})`);
    }

    return { response, payload };
  };

  const buildPayloads = () => {
    const trimmedFirstName = firstName.trim();
    const trimmedLastName = lastName.trim();
    const trimmedEmployment = employment.trim();
    const trimmedPersonFirstName = personFirstName.trim() || trimmedFirstName;
    const trimmedPersonLastName = personLastName.trim() || trimmedLastName;
    const trimmedDateOfBirth = dateOfBirth.trim();
    const trimmedAddress = address.trim();
    const trimmedLegalSex = legalSex.trim();
    const trimmedBusinessName = businessName.trim();
    const trimmedPositionName = positionName.trim();
    const trimmedSalary = salary.trim();

    const missingFields = [];
    if (!trimmedFirstName) missingFields.push("client first name");
    if (!trimmedLastName) missingFields.push("client last name");
    if (!trimmedEmployment) missingFields.push("employment type/status");
    if (!trimmedDateOfBirth) missingFields.push("date of birth");
    if (!trimmedAddress) missingFields.push("address");
    if (!trimmedLegalSex) missingFields.push("legal sex");
    if (!trimmedBusinessName) missingFields.push("business name");
    if (!trimmedPositionName) missingFields.push("position name");
    if (!trimmedSalary) missingFields.push("salary");

    if (missingFields.length > 0) {
      setStatus(`Error: Missing required fields: ${missingFields.join(", ")}.`);
      return null;
    }

    const numericSalary = Number(trimmedSalary);
    if (Number.isNaN(numericSalary) || numericSalary < 0) {
      setStatus("Error: salary must be a non-negative number.");
      return null;
    }

    const normalizedDateOfBirth = normalizeDateOfBirth(trimmedDateOfBirth);
    if (!normalizedDateOfBirth) {
      setStatus("Error: date of birth must be a valid date in YYYY-MM-DD format.");
      return null;
    }

    return {
      clientPayload: {
        firstName: trimmedFirstName,
        lastName: trimmedLastName,
        employment: trimmedEmployment,
      },
      personPayload: {
        firstName: trimmedPersonFirstName,
        lastName: trimmedPersonLastName,
        dateOfBirth: normalizedDateOfBirth,
        address: trimmedAddress,
        legalSex: trimmedLegalSex,
      },
      employmentPayload: {
        businessName: trimmedBusinessName,
        positionName: trimmedPositionName,
        salary: numericSalary,
      },
    };
  };

  const fetchAndDisplayClient = async (id) => {
    const { payload } = await sendRequest(`/api/clients/${id}`, { method: "GET" });
    setResultData(payload);
    return payload;
  };

  const handleView = async () => {
    setLoading(true);
    try {
      if (clientId.trim()) {
        const id = clientId.trim();
        await fetchAndDisplayClient(id);
        setStatus(`Loaded client #${id}.`);
      } else {
        const { payload } = await sendRequest("/api/clients", { method: "GET" });
        setResultData(payload);
        if (Array.isArray(payload)) {
          setStatus(`Loaded ${payload.length} client record(s).`);
        } else {
          setStatus("Loaded client data.");
        }
      }
    } catch (error) {
      setResultData(null);
      setStatus(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    const payloads = buildPayloads();
    if (!payloads) return;

    setLoading(true);
    setResultData(null);
    let createdClientId = null;

    try {
      const { payload: createdClient } = await sendRequest("/api/clients", {
        method: "POST",
        body: JSON.stringify(payloads.clientPayload),
      });

      createdClientId = createdClient?.clientId;
      if (!createdClientId) {
        throw new Error("Client created, but no clientId was returned.");
      }

      await sendRequest(`/api/clients/${createdClientId}/person`, {
        method: "PUT",
        body: JSON.stringify(payloads.personPayload),
      });

      await sendRequest(`/api/clients/${createdClientId}/employment`, {
        method: "PUT",
        body: JSON.stringify(payloads.employmentPayload),
      });

      await fetchAndDisplayClient(createdClientId);
      setStatus(`Created client #${createdClientId} with person and employment details.`);
    } catch (error) {
      if (createdClientId) {
        try {
          await sendRequest(`/api/clients/${createdClientId}`, { method: "DELETE" });
          setStatus(
            `Error: related details failed for client #${createdClientId}. Rolled back that client to avoid partial data.`
          );
        } catch (cleanupError) {
          setStatus(
            `Error: client #${createdClientId} was created, related details failed (${error.message}), and rollback failed (${cleanupError.message}).`
          );
        }
      } else {
        setStatus(`Error: ${error.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleModify = async () => {
    if (!clientId.trim()) {
      setStatus("Error: clientId is required for MODIFY.");
      return;
    }

    const payloads = buildPayloads();
    if (!payloads) return;

    setLoading(true);
    setResultData(null);
    const id = clientId.trim();

    try {
      await sendRequest(`/api/clients/${id}`, {
        method: "PUT",
        body: JSON.stringify(payloads.clientPayload),
      });

      await sendRequest(`/api/clients/${id}/person`, {
        method: "PUT",
        body: JSON.stringify(payloads.personPayload),
      });

      await sendRequest(`/api/clients/${id}/employment`, {
        method: "PUT",
        body: JSON.stringify(payloads.employmentPayload),
      });

      await fetchAndDisplayClient(id);
      setStatus(`Updated client #${id}, including person and employment details.`);
    } catch (error) {
      setStatus(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!clientId.trim()) {
      setStatus("Error: clientId is required for DELETE.");
      return;
    }

    setLoading(true);
    setResultData(null);
    try {
      const id = clientId.trim();
      const { response } = await sendRequest(`/api/clients/${id}`, { method: "DELETE" });
      if (response.status === 204) {
        setStatus(`Deleted client #${id}.`);
      } else {
        setStatus("Client deleted.");
      }
    } catch (error) {
      setStatus(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setClientId("");
    setFirstName("");
    setLastName("");
    setEmployment("");
    setPersonFirstName("");
    setPersonLastName("");
    setDateOfBirth("");
    setAddress("");
    setLegalSex("");
    setBusinessName("");
    setPositionName("");
    setSalary("");
    setStatus("Cleared form fields.");
    setResultData(null);
  };

  const renderClientCard = (client, idx) => {
    const person = client.person;
    const employmentDetails = client.employmentDetails;

    return (
      <article
        key={client.clientId ?? `client-${idx}`}
        className="client-card"
      >
        <div className="client-card-header">
          <h3>Client #{formatField(client.clientId)}</h3>
          <span className="pill">{formatField(client.employment)}</span>
        </div>

        <div className="kv-grid">
          <div className="kv-item">
            <span>First Name</span>
            <strong>{formatField(client.firstName)}</strong>
          </div>
          <div className="kv-item">
            <span>Last Name</span>
            <strong>{formatField(client.lastName)}</strong>
          </div>
        </div>

        <div className="details-grid">
          <section className="detail-card">
            <h4>Person Details</h4>
            {person ? (
              <div className="detail-list">
                <p><b>Name:</b> {formatField(person.firstName)} {formatField(person.lastName)}</p>
                <p><b>Date of Birth:</b> {formatField(person.dateOfBirth)}</p>
                <p><b>Address:</b> {formatField(person.address)}</p>
                <p><b>Legal Sex:</b> {formatField(person.legalSex)}</p>
              </div>
            ) : (
              <p className="empty-text">No person record on file.</p>
            )}
          </section>

          <section className="detail-card">
            <h4>Employment Details</h4>
            {employmentDetails ? (
              <div className="detail-list">
                <p><b>Business:</b> {formatField(employmentDetails.businessName)}</p>
                <p><b>Position:</b> {formatField(employmentDetails.positionName)}</p>
                <p><b>Salary:</b> {formatSalary(employmentDetails.salary)}</p>
              </div>
            ) : (
              <p className="empty-text">No employment record on file.</p>
            )}
          </section>
        </div>
      </article>
    );
  };

  const renderResult = () => {
    if (loading) return <p className="status-panel">Working...</p>;
    if (!resultData) return null;

    if (Array.isArray(resultData) && resultData.every(isClientPayload)) {
      if (resultData.length === 0) {
        return <p className="status-panel">No clients found.</p>;
      }
      return (
        <div className="result-list">
          {resultData.map((client, idx) => renderClientCard(client, idx))}
        </div>
      );
    }

    if (isClientPayload(resultData)) {
      return <div className="result-list">{renderClientCard(resultData, 0)}</div>;
    }

    if (typeof resultData === "string") {
      return <p className="status-panel">{resultData}</p>;
    }

    return <pre>{JSON.stringify(resultData, null, 2)}</pre>;
  };

  return (
    <main className="page">
      <p className="render-note">
        Since this is a free Render project, it can take up to 50 seconds to run the backend from idle. If nothing happens, refresh and try the button again.
      </p>
      <h1>Client CRUD</h1>

      <section className="form-section form-section-client">
        <h2>Client Record</h2>
        <label htmlFor="clientId">Client ID (use for single VIEW, MODIFY, DELETE)</label>
        <input
          id="clientId"
          type="number"
          value={clientId}
          onChange={(e) => setClientId(e.target.value)}
          placeholder="e.g. 1"
        />

        <label htmlFor="firstName">Client First Name (CREATE/MODIFY)</label>
        <input
          id="firstName"
          type="text"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          placeholder="e.g. Jane"
        />

        <label htmlFor="lastName">Client Last Name (CREATE/MODIFY)</label>
        <input
          id="lastName"
          type="text"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          placeholder="e.g. Doe"
        />

        <label htmlFor="employment">Employment Type / Status (CREATE/MODIFY)</label>
        <input
          id="employment"
          type="text"
          value={employment}
          onChange={(e) => setEmployment(e.target.value)}
          placeholder="e.g. W2"
        />
      </section>

      <section className="form-section form-section-person">
        <h2>Person Details</h2>
        <label htmlFor="personFirstName">Person First Name (optional, defaults to client first name)</label>
        <input
          id="personFirstName"
          type="text"
          value={personFirstName}
          onChange={(e) => setPersonFirstName(e.target.value)}
          placeholder="e.g. Jane"
        />

        <label htmlFor="personLastName">Person Last Name (optional, defaults to client last name)</label>
        <input
          id="personLastName"
          type="text"
          value={personLastName}
          onChange={(e) => setPersonLastName(e.target.value)}
          placeholder="e.g. Doe"
        />

        <label htmlFor="dateOfBirth">Date of Birth (MM/DD/YYY)</label> {/* (picker may display MM/DD/YYYY, sent as YYYY-MM-DD) */}
        <input
          id="dateOfBirth"
          type="date"
          value={dateOfBirth}
          onChange={(e) => setDateOfBirth(e.target.value)}
        />

        <label htmlFor="address">Address</label>
        <input
          id="address"
          type="text"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          placeholder="e.g. 123 Main St, Columbia, SC 29201"
        />

        <label htmlFor="legalSex">Legal Sex</label>
        <input
          id="legalSex"
          type="text"
          value={legalSex}
          onChange={(e) => setLegalSex(e.target.value)}
          placeholder="e.g. Female"
        />
      </section>

      <section className="form-section form-section-employment">
        <h2>Employment Details</h2>
        <label htmlFor="businessName">Business Name</label>
        <input
          id="businessName"
          type="text"
          value={businessName}
          onChange={(e) => setBusinessName(e.target.value)}
          placeholder="e.g. Garnet Health Group"
        />

        <label htmlFor="positionName">Position Name</label>
        <input
          id="positionName"
          type="text"
          value={positionName}
          onChange={(e) => setPositionName(e.target.value)}
          placeholder="e.g. Analyst"
        />

        <label htmlFor="salary">Salary</label>
        <input
          id="salary"
          type="number"
          min="0"
          step="0.01"
          value={salary}
          onChange={(e) => setSalary(e.target.value)}
          placeholder="e.g. 78000"
        />
      </section>

      <div className="buttons">
        <button onClick={handleView} disabled={loading}>VIEW</button>
        <button onClick={handleCreate} disabled={loading}>CREATE</button>
        <button onClick={handleModify} disabled={loading}>MODIFY</button>
        <button onClick={handleDelete} disabled={loading}>DELETE</button>
        <button onClick={handleClear} disabled={loading}>Clear</button>
      </div>

      <h2>Response</h2>
      <p className={`status-panel ${status.startsWith("Error:") ? "error" : "ok"}`}>{status}</p>
      {renderResult()}
    </main>
  );
}
