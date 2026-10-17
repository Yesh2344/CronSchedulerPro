# CronSchedulerPro

[![Build Status](https://img.shields.io/github/actions/workflow/status/yourrepo/cron-scheduler-pro/build.yml?branch=main)](https://github.com/yourrepo/cron-scheduler-pro/actions)
[![License](https://img.shields.io/github/license/yourrepo/cron-scheduler-pro)](LICENSE)

CronSchedulerPro is a lightweight, production‑ready Java library that allows you to schedule jobs using **cron expressions**. 
It provides:

* **Configuration handling** via `application.properties` and environment variables.
* **Dependency injection‑friendly** design using the Singleton and Factory patterns.
* **Robust error handling** and **SLF4J** logging.
* **Extensible job model** – simply implement `Job` and register it.
* **Unit tests** with JUnit.

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Creating Custom Jobs](#creating-custom-jobs)
- [Testing](#testing)
- [License](#license)

## Installation

The project uses **Gradle**. Clone the repository and build:

<!-- was easier to read this way -->
