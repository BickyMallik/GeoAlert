# GeoAlert 🚨

A real-time disaster alert notification system built with Spring Boot and PostGIS.

## Features
- Radius-based geo-filtering using PostGIS ST_DWithin spatial queries
- Real-time WebSocket broadcast to affected users
- AI-generated safety instructions via OpenRouter API
- Automated weather monitoring with OpenWeatherMap scheduler
- Notification persistence for audit trail

## Tech Stack
- Spring Boot 3
- PostgreSQL + PostGIS
- Hibernate Spatial + JTS
- WebSocket (STOMP)
- OpenRouter AI API
- OpenWeatherMap API
- Maven

## How It Works
1. Alert created with location coordinates and radius
2. PostGIS finds all users within the radius
3. AI generates disaster-specific safety instructions
4. WebSocket broadcasts alert to affected users in real time
5. Scheduler auto-creates alerts from live weather data every 60 seconds

## Author
Bicky Mallik — B.Tech CSE, BBIT Kolkata
