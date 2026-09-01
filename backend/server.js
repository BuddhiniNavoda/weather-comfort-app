require("dotenv").config();

const express = require("express");
const fs = require("fs");
const path = require("path");

const app = express();
const PORT = process.env.PORT || 3001;
const API_KEY = process.env.OPENWEATHER_API_KEY;

function readCityIds() {
  const filePath = path.join(__dirname, "cities.json");
  const raw = fs.readFileSync(filePath, "utf8");
  const parsed = JSON.parse(raw);
  const list = parsed.List || [];

  return list.map((city) => city.CityCode);
}

async function fetchWeather(cityId) {
  const url =
    "https://api.openweathermap.org/data/2.5/weather" +
    `?id=${cityId}&appid=${API_KEY}&units=metric`;

  const response = await fetch(url);
  const data = await response.json();

  if (!response.ok) {
    throw new Error(
      data.message || `OpenWeatherMap error (${response.status}) for city ${cityId}`
    );
  }

  return data;
}

app.get("/api/weather", async (_req, res) => {
  try {
    if (!API_KEY || API_KEY === "paste_your_key_here") {
      return res.status(500).json({
        error: "Add your OpenWeatherMap key to backend/.env as OPENWEATHER_API_KEY",
      });
    }

    const cityIds = readCityIds();
    const cities = [];

    for (const cityId of cityIds) {
      const data = await fetchWeather(cityId);

      cities.push({
        id: data.id,
        name: data.name,
        country: data.sys && data.sys.country,
        description: data.weather && data.weather[0] && data.weather[0].description,
        temperature: data.main && data.main.temp,
        humidity: data.main && data.main.humidity,
        windSpeed: data.wind && data.wind.speed,
        clouds: data.clouds && data.clouds.all,
      });
    }

    res.json({ count: cities.length, cities });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`Backend running: http://localhost:${PORT}/api/weather`);
});
