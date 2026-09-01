import { useEffect, useMemo, useState } from "react";

export default function App() {
  const [theme, setTheme] = useState(() => localStorage.getItem("theme") || "light");
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [query, setQuery] = useState("");
  const [sortBy, setSortBy] = useState("score");

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    getAccessTokenSilently()
      .then((token) =>
        fetch("/api/weather", {
          headers: { Authorization: "Bearer " + token },
        })
      )
      .then((res) => {
        if (res.status === 401) {
          throw new Error("Login expired. Log in again.");
        }
        if (!res.ok) {
          return res.json().then((body) => {
            throw new Error(body.error || "Could not load weather");
          });
        }
        return res.json();
      })
      .then((body) => {
        if (ignore) return;
        setData(body);
        setError("");
      })
      .catch((err) => {
        if (!ignore) {
          setError(springDown ? msg + " Start the Spring Boot app on port 8080." : msg);
        }
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, []);

  const cities = useMemo(() => {
    const list = data?.cities ? [...data.cities] : [];
    const q = query.trim().toLowerCase();
    const filtered = q
      ? list.filter(
          (c) =>
            c.name.toLowerCase().includes(q) ||
            (c.country && c.country.toLowerCase().includes(q))
        )
      : list;

    filtered.sort((a, b) => {
      if (sortBy === "name") return a.name.localeCompare(b.name);
      if (sortBy === "temperature") return (b.temperature || 0) - (a.temperature || 0);
      return (b.score || 0) - (a.score || 0);
    });
    return filtered;
  }, [data, query, sortBy]);

  const maxTemp = Math.max(1, ...cities.map((c) => Math.abs(c.temperature || 0)));

  return (
    <div className="page">
      <header className="top">
        <h1>Weather dashboard</h1>
        <div className="header-actions">
          <span className="muted">{user?.email}</span>
          <button type="button" className="theme-btn" onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>
            {theme === "dark" ? "Light mode" : "Dark mode"}
          </button>
          <button
            type="button"
            className="theme-btn"
            onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
          >
            Log out
          </button>
        </div>
      </header>

      {loading && <p>Loading weather...</p>}
      {error && <p className="error">{error}</p>}

      {data && (
        <>
          <div className="toolbar">
            <input
              type="search"
              placeholder="Filter by city or country"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="score">Sort by score</option>
              <option value="temperature">Sort by temperature</option>
              <option value="name">Sort by name</option>
            </select>
          </div>

          <section className="table-wrap desktop-only">
            <table>
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>City</th>
                  <th>Weather</th>
                  <th>Temp</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {cities.map((city) => (
                  <tr key={city.id}>
                    <td>{city.rank}</td>
                    <td>
                      {city.name}, {city.country}
                    </td>
                    <td>{city.description}</td>
                    <td>{city.temperature}°C</td>
                    <td>{city.score}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </section>

          <section className="cards mobile-only">
            {cities.map((city) => (
              <article key={city.id} className="card">
                <div className="card-top">
                  <strong>
                    #{city.rank} {city.name}
                  </strong>
                  <span>{city.country}</span>
                </div>
                <p>{city.description}</p>
                <p>
                  {city.temperature}°C · score {city.score}
                </p>
              </article>
            ))}
          </section>

          <section className="chart-box">
            <h2>Temperature by city</h2>
            <div className="bars" aria-label="Temperature by city">
              {cities.map((city) => (
                <div key={city.id} className="bar-col">
                  <span className="bar-value">{city.temperature}°</span>
                  <div
                    className="bar"
                    style={{
                      height: `${Math.max(8, (Math.abs(city.temperature || 0) / maxTemp) * 140)}px`,
                    }}
                    title={`${city.name}: ${city.temperature}°C`}
                  />
                  <span className="bar-label">{city.name}</span>
                </div>
              ))}
            </div>
          </section>
        </>
      )}
    </div>
  );
}
