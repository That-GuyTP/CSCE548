import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import App from "./App";

const jsonResponse = (payload) => ({
  ok: true,
  status: 200,
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

  test("CREATE calls POST /api/clients with form body", async () => {
    global.fetch.mockResolvedValueOnce(jsonResponse({ clientId: 99 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText(/First Name/i), {
      target: { value: "Jane" },
    });
    fireEvent.change(screen.getByLabelText(/Last Name/i), {
      target: { value: "Doe" },
    });
    fireEvent.change(screen.getByLabelText(/Employment/i), {
      target: { value: "W2" },
    });
    fireEvent.click(screen.getByRole("button", { name: "CREATE" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
    expect(global.fetch).toHaveBeenCalledWith(
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
  });

  test("MODIFY calls PUT /api/clients/{id} with form body", async () => {
    global.fetch.mockResolvedValueOnce(jsonResponse({ clientId: 1 }));

    render(<App />);

    fireEvent.change(screen.getByLabelText(/Client ID/i), {
      target: { value: "1" },
    });
    fireEvent.change(screen.getByLabelText(/First Name/i), {
      target: { value: "Ava" },
    });
    fireEvent.change(screen.getByLabelText(/Last Name/i), {
      target: { value: "Johnson" },
    });
    fireEvent.change(screen.getByLabelText(/Employment/i), {
      target: { value: "1099" },
    });
    fireEvent.click(screen.getByRole("button", { name: "MODIFY" }));

    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
    expect(global.fetch).toHaveBeenCalledWith(
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
});
