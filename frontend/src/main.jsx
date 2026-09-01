import React from "react";
import ReactDOM from "react-dom/client";
import { Auth0Provider } from "@auth0/auth0-react";
import App from "./App.jsx";
import "./index.css";

const domain = import.meta.env.VITE_AUTH0_DOMAIN;
const clientId = import.meta.env.VITE_AUTH0_CLIENT_ID;
const audience = import.meta.env.VITE_AUTH0_AUDIENCE;

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
  <App />
    {domain && clientId && audience ? (
      <Auth0Provider
        domain={domain}
        clientId={clientId}
        authorizationParams={{
          redirect_uri: window.location.origin,
          audience: audience,
          scope: "openid profile email",
        }}
      >
        <App />
      </Auth0Provider>
    ) : (
      <p style={{ padding: 24, fontFamily: "sans-serif" }}>
        Create frontend/.env from frontend/.env.example and restart npm run dev.
      </p>
    )}
  </React.StrictMode>
);
