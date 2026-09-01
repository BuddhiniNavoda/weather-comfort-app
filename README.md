# Weather App

Spring Boot backend that reads city IDs from `cities.json` and loads live weather from OpenWeatherMap.

Weather endpoint:

```text
GET http://localhost:8080/api/weather
GET http://localhost:8080/api/cache/status
```

## Frontend

The UI only **displays** backend `score` and `rank`. It does not calculate comfort.

```bash
cd frontend
npm install
npm run dev
```
Comfort Index unit tests:

```bash
cd backend
mvn test
```

Open http://localhost:5173. Keep Spring Boot running on port 8080.


The **Comfort Index** is calculated on the **backend only**. The frontend must not calculate the score. This file explains the formula in full: definition, equations, and why we chose this logic.

## Comfort Index — complete definition

### What it is

The Comfort Index is a number from **0 to 100** for one city at one moment.

| Score | Meaning |
|-------|--------|
| 100 | Very comfortable outdoors |
| 50 | Okay / mixed |
| 0 | Very uncomfortable |

It is **not** a medical index. It is **our** rule to compare cities using live weather.

We only use data we already get from OpenWeatherMap:

- `temperature` in °C
- `humidity` in %
- `windSpeed` in m/s (metres per second)

We do **not** use `Temp` or `Status` from `cities.json`. Those values in the file are old samples. City IDs come from `cities.json`. Weather numbers come from OpenWeatherMap.

### High-level algorithm

1. For each city, compute three sub-scores: temperature, humidity, wind. Each sub-score is 0–100.
2. Combine them with weights that add up to **1.0**.
3. Clamp the final score to 0–100.
4. Sort cities from **highest score to lowest**.
5. Give **rank** 1 to the most comfortable city, then 2, 3, and so on.

```text
comfortScore = (0.50 × tempScore) + (0.30 × humidityScore) + (0.20 × windScore)
```

Then:

```text
comfortScore = min(100, max(0, comfortScore))
```

---

## Why this shape of formula?

Raw weather numbers cannot be added as they are.

- Temperature is about 13–31 in our list.
- Humidity is about 68–94.
- Wind is about 0.9–6.2.

If we wrote `score = temperature + humidity + wind`, humidity would dominate because it is a large number. That would be a bad formula.

So we do two steps:

1. **Normalise** each factor to a 0–100 score (“how close to comfortable?”).
2. **Weight** those scores (temperature matters most).

Every sub-score uses the same idea:

```text
score = 100 − (how far from nice weather) × (how strict we are)
```

Then we limit the result to 0–100.

---

## 1. Temperature score

**Definition**

```text
tempScore = 100 − |temperature − 22| × 5
tempScore = min(100, max(0, tempScore))
```

`|x|` means absolute value: the gap, whether the city is hotter or colder than 22°C.

**Ideal value: 22°C**

We treat 22°C as mild outdoor weather. It is a design choice, not a law. We use it so every city is judged on the same scale.

**Logic of `|temperature − 22|`**

Too hot and too cold are both less comfortable. 12°C and 32°C are both 10°C away from 22, so they get the same temperature penalty.

**Logic of × 5**

The gap is in degrees. If we only subtract the gap:

```text
Colombo 30.58°C → 100 − 8.58 ≈ 91
```

91 would mean “almost perfect”. That is too kind for 30°C.

× 5 means: **each 1°C away from 22 costs 5 points**.

- 22°C → 100
- 23°C or 21°C → 95
- about 32°C or 12°C → 50
- 20°C away from 22 → 0

So 5 is how fast heat or cold becomes a low score.

**Example (Colombo 30.58°C)**

```text
|30.58 − 22| = 8.58
tempScore = 100 − 8.58 × 5 = 57.1
```

---

## 2. Humidity score

**Definition**

```text
humidityScore = 100 − |humidity − 50| × 1.5
humidityScore = min(100, max(0, humidityScore))
```

**Ideal value: 50%**

Around 40–60% humidity is often described as comfortable air: not very dry, not very sticky. We use 50% as the centre of that idea.

**Logic of `|humidity − 50|`**

Too dry and too sticky both lose points.

**Logic of × 1.5 (not × 5)**

Humidity already runs from 0 to 100. If we used × 5 like temperature, most cities would get a very low humidity score. Example: 74% humidity would lose `24 × 5 = 120` points and hit 0.

× 1.5 is milder:

- 50% → 100
- 60% or 40% → 85
- 74% → 64
- 0% or 100% → 25

So humidity still matters, but it does not wipe out the whole score the way × 5 would.

**Example (Colombo 74%)**

```text
|74 − 50| = 24
humidityScore = 100 − 24 × 1.5 = 64
```

---

## 3. Wind score

**Definition**

```text
windScore = 100 − windSpeed × 10
windScore = min(100, max(0, windScore))
```

Wind from OpenWeatherMap is in **m/s**.

**Logic (simple version)**

We treat **more wind as less comfortable** (harder to walk, colder feel). Calm air starts at 100. We do not use `|wind − 2|` in this first version. That keeps the formula easy to explain and easy to change in an interview.

**Logic of × 10**

Typical wind in our cities is about 1–6 m/s. If we only did `100 − windSpeed`:

- Colombo 5.84 → about 94

Wind would almost not change the ranking. × 10 makes wind visible:

- 0 m/s → 100
- 1 m/s → 90
- 5.84 m/s → 41.6
- 10 m/s → 0

So 10 means: strong wind (about 10 m/s) is the bottom of the scale. Everyday breeze still sits in the middle.

**Example (Colombo 5.84 m/s)**

```text
windScore = 100 − 5.84 × 10 = 41.6
```

---

## 4. Final Comfort Index

**Definition**

```text
comfortScore = 0.50 × tempScore + 0.30 × humidityScore + 0.20 × windScore
comfortScore = min(100, max(0, comfortScore))
```

**Why these weights?**

| Factor | Weight | Reason |
|--------|--------|--------|
| Temperature | 0.50 | Heat and cold are what people notice first. |
| Humidity | 0.30 | Sticky or dry air changes comfort, but usually less than temperature. |
| Wind | 0.20 | Wind adjusts the feel. It should not beat temperature. |

The weights add to **1.0**, so the result stays on a 0–100 scale if each sub-score is 0–100.

**Full Colombo example**

```text
tempScore      = 57.1
humidityScore  = 64
windScore      = 41.6

comfortScore = 0.50×57.1 + 0.30×64 + 0.20×41.6
             = 28.55 + 19.2 + 8.32
             = 56.07
```

Round to **2 decimal places** when we show it in the API (for example `56.07`).

---

## Ranking

After every city has a `comfortScore`:

1. Sort by `comfortScore` descending (highest first).
2. Rank 1 = highest score, rank 2 = next, and so on.

If two scores are equal, keep a stable order (for example keep the original city order).

---

## Why the backend must do this

- One formula for every user.
- The UI cannot fake a better rank in the browser.
- The API key and the score stay on the server.
- In a live interview we can change weights or add `clouds` in one backend file.

The frontend should only **display** `score` and `rank`.

---

## Trade-offs (important)

- **22°C and 50% humidity are our defaults.** Someone in Colombo may feel fine at 30°C. This formula compares cities on one global “mild outdoor” scale. It is not local climate.
- **Wind rule is simple.** A little wind can feel good in heat. Version 1 ignores that so the math stays clear. We can add it later.
- **OpenWeatherMap current weather does not change every second.** Scores can look the same if you refresh quickly. That is the weather data, not a frozen `cities.json` file.
- **In-memory cache is lost when the server restarts.** After you change the comfort formula, restart the app so old ranks are not kept for 5 minutes.

---

## Server cache (5 minutes)

Weather calls are cached **on the server**, not in the browser.

**Raw cache:** OpenWeatherMap JSON per city ID.  
**Processed cache:** the full ranked list (score + rank). Preferred, because a HIT skips OpenWeatherMap and the formula.

TTL is 300 seconds (`cache.ttl-seconds` in `application.properties`).

```text
GET /api/weather
  → processed HIT  → return saved list
  → processed MISS → for each city, raw HIT or call OpenWeatherMap
                   → calculate scores, rank, save processed, return
```

**HIT** = saved data is younger than 5 minutes.  
**MISS** = empty or older than 5 minutes.

Debug:

```text
GET http://localhost:8080/api/cache/status
```

`lastWeatherRequest` is from the last call to `/api/weather`. `processed` and `raw` show what is in memory now.

How to test: restart the app, call `/api/weather` (MISS), call it again (HIT), wait 5 minutes (MISS).

---

## How we would add a fourth parameter later

Example: clouds (0–100 from the API).

```text
cloudScore = 100 − clouds
comfortScore = 0.40×tempScore + 0.25×humidityScore + 0.20×windScore + 0.15×cloudScore
```

Weights still add to 1.0. Ranks can change. That is expected.

---
