import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import App from "./App";

const jsonResponse = (payload, status = 200) => ({
  ok: status >= 200 && status < 300,
  status,
  statusText: "OK",
  headers: {
    get: (name) => (name === "content-type" ? "application/json" : null),
  },
  json: async () => payload,
  text: async () => JSON.stringify(payload),
});

describe("Client CRUD buttons", () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  test("VIEW calls GET /api/clients/{id} when client id is provided", async () => {
    global.fetch.mockResolvedValueOnce(jsonResponse({ clientId: 1 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText(/Client ID/i), {
      target: { value: "1" },
    });
    fireEvent.click(screen.getByRole("button", { name: "VIEW" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
    expect(global.fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/clients/1",
      { method: "GET" }
    );
  });

  test("CREATE creates client, person, employment, then reloads client", async () => {
    global.fetch
      .mockResolvedValueOnce(jsonResponse({ clientId: 99 }, 201))
      .mockResolvedValueOnce(jsonResponse({ clientId: 99 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 99 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 99 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText("Client First Name"), {
      target: { value: "Jane" },
    });
    fireEvent.change(screen.getByLabelText("Client Last Name"), {
      target: { value: "Doe" },
    });
    fireEvent.change(screen.getByLabelText(/Employment Type/i), {
      target: { value: "W2" },
    });
    fireEvent.change(screen.getByLabelText(/Date of Birth/i), {
      target: { value: "1990-01-01" },
    });
    fireEvent.change(screen.getByLabelText(/^Address$/i), {
      target: { value: "123 Main St, Columbia, SC 29201" },
    });
    fireEvent.change(screen.getByLabelText(/Legal Sex/i), {
      target: { value: "Female" },
    });
    fireEvent.change(screen.getByLabelText(/Business Name/i), {
      target: { value: "Garnet Health Group" },
    });
    fireEvent.change(screen.getByLabelText(/Position Name/i), {
      target: { value: "Analyst" },
    });
    fireEvent.change(screen.getByLabelText(/^Salary$/i), {
      target: { value: "78000" },
    });

    fireEvent.click(screen.getByRole("button", { name: "CREATE" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(4));
    expect(global.fetch).toHaveBeenNthCalledWith(
      1,
      "http://localhost:8080/api/clients",
      {
        method: "POST",
        body: JSON.stringify({
          firstName: "Jane",
          lastName: "Doe",
          employment: "W2",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      2,
      "http://localhost:8080/api/clients/99/person",
      {
        method: "PUT",
        body: JSON.stringify({
          firstName: "Jane",
          lastName: "Doe",
          dateOfBirth: "1990-01-01",
          address: "123 Main St, Columbia, SC 29201",
          legalSex: "Female",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      3,
      "http://localhost:8080/api/clients/99/employment",
      {
        method: "PUT",
        body: JSON.stringify({
          businessName: "Garnet Health Group",
          positionName: "Analyst",
          salary: 78000,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      4,
      "http://localhost:8080/api/clients/99",
      { method: "GET" }
    );
  });

  test("MODIFY updates client, person, employment, then reloads client", async () => {
    global.fetch
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText(/Client ID/i), {
      target: { value: "1" },
    });
    fireEvent.change(screen.getByLabelText("Client First Name"), {
      target: { value: "Ava" },
    });
    fireEvent.change(screen.getByLabelText("Client Last Name"), {
      target: { value: "Johnson" },
    });
    fireEvent.change(screen.getByLabelText(/Employment Type/i), {
      target: { value: "1099" },
    });
    fireEvent.change(screen.getByLabelText(/Date of Birth/i), {
      target: { value: "1988-06-15" },
    });
    fireEvent.change(screen.getByLabelText(/^Address$/i), {
      target: { value: "77 Sunset Blvd, Columbia, SC 29205" },
    });
    fireEvent.change(screen.getByLabelText(/Legal Sex/i), {
      target: { value: "Female" },
    });
    fireEvent.change(screen.getByLabelText(/Business Name/i), {
      target: { value: "Capital City Services" },
    });
    fireEvent.change(screen.getByLabelText(/Position Name/i), {
      target: { value: "Manager" },
    });
    fireEvent.change(screen.getByLabelText(/^Salary$/i), {
      target: { value: "92000" },
    });

    fireEvent.click(screen.getByRole("button", { name: "MODIFY" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(4));
    expect(global.fetch).toHaveBeenNthCalledWith(
      1,
      "http://localhost:8080/api/clients/1",
      {
        method: "PUT",
        body: JSON.stringify({
          firstName: "Ava",
          lastName: "Johnson",
          employment: "1099",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      2,
      "http://localhost:8080/api/clients/1/person",
      {
        method: "PUT",
        body: JSON.stringify({
          dateOfBirth: "1988-06-15",
          address: "77 Sunset Blvd, Columbia, SC 29205",
          legalSex: "Female",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      3,
      "http://localhost:8080/api/clients/1/employment",
      {
        method: "PUT",
        body: JSON.stringify({
          businessName: "Capital City Services",
          positionName: "Manager",
          salary: 92000,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      4,
      "http://localhost:8080/api/clients/1",
      { method: "GET" }
    );
  });

  test("MODIFY sends only fields that have update values", async () => {
    global.fetch
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }))
      .mockResolvedValueOnce(jsonResponse({ clientId: 1 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText(/Client ID/i), {
      target: { value: "1" },
    });
    fireEvent.change(screen.getByLabelText(/^Address$/i), {
      target: { value: "88 Main St, Columbia, SC 29201" },
    });

    fireEvent.click(screen.getByRole("button", { name: "MODIFY" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));
    expect(global.fetch).toHaveBeenNthCalledWith(
      1,
      "http://localhost:8080/api/clients/1/person",
      {
        method: "PUT",
        body: JSON.stringify({
          address: "88 Main St, Columbia, SC 29201",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    expect(global.fetch).toHaveBeenNthCalledWith(
      2,
      "http://localhost:8080/api/clients/1",
      { method: "GET" }
    );
  });

  test("DELETE calls DELETE /api/clients/{id}", async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      status: 204,
      statusText: "No Content",
      headers: { get: () => "text/plain" },
      text: async () => "",
      json: async () => ({}),
    });

    render(<App />);

    fireEvent.change(screen.getByLabelText(/Client ID/i), {
      target: { value: "1" },
    });
    fireEvent.click(screen.getByRole("button", { name: "DELETE" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
    expect(global.fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/clients/1",
      { method: "DELETE" }
    );
  });

  test("Clear empties all input fields", () => {
    render(<App />);

    const clientIdInput = screen.getByLabelText(/Client ID/i);
    const firstNameInput = screen.getByLabelText("Client First Name");
    const lastNameInput = screen.getByLabelText("Client Last Name");
    const employmentInput = screen.getByLabelText(/Employment Type/i);
    const dateOfBirthInput = screen.getByLabelText(/Date of Birth/i);
    const addressInput = screen.getByLabelText(/^Address$/i);
    const legalSexInput = screen.getByLabelText(/Legal Sex/i);
    const businessNameInput = screen.getByLabelText(/Business Name/i);
    const positionNameInput = screen.getByLabelText(/Position Name/i);
    const salaryInput = screen.getByLabelText(/^Salary$/i);

    fireEvent.change(clientIdInput, { target: { value: "1" } });
    fireEvent.change(firstNameInput, { target: { value: "Jane" } });
    fireEvent.change(lastNameInput, { target: { value: "Doe" } });
    fireEvent.change(employmentInput, { target: { value: "W2" } });
    fireEvent.change(dateOfBirthInput, { target: { value: "1990-01-01" } });
    fireEvent.change(addressInput, { target: { value: "123 Main St" } });
    fireEvent.change(legalSexInput, { target: { value: "Female" } });
    fireEvent.change(businessNameInput, { target: { value: "Garnet Health Group" } });
    fireEvent.change(positionNameInput, { target: { value: "Analyst" } });
    fireEvent.change(salaryInput, { target: { value: "78000" } });

    fireEvent.click(screen.getByRole("button", { name: "Clear Inputs" }));

    expect(clientIdInput.value).toBe("");
    expect(firstNameInput.value).toBe("");
    expect(lastNameInput.value).toBe("");
    expect(employmentInput.value).toBe("");
    expect(dateOfBirthInput.value).toBe("");
    expect(addressInput.value).toBe("");
    expect(legalSexInput.value).toBe("");
    expect(businessNameInput.value).toBe("");
    expect(positionNameInput.value).toBe("");
    expect(salaryInput.value).toBe("");
  });
});
