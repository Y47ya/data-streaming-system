# Real-Time Data Streaming with Sockets (CSV & Parquet)

## Overview
This project implements a real-time data streaming system using socket programming. It enables streaming of structured data from CSV and Parquet files between a producer and a consumer over a TCP connection.

The goal is to simulate a basic data pipeline where data is continuously read, transmitted, and processed in real time.

---

## Features
- Stream data from CSV files
- Stream data from Parquet files
- TCP socket-based communication
- Real-time data transmission
- Simple producer-consumer architecture
- Easily extendable for data processing or storage

---

## Architecture

Producer (CSV / Parquet Reader)  --->  TCP Socket  --->  Consumer (Receiver / Processor)

- Producer: Reads data from files and sends it over a socket.
- Consumer: Receives the streamed data and processes it.

---

## Technologies Used
- Python
- Socket Programming
- Pandas
- PyArrow (for Parquet support)

---
