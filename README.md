# Java System Monitoring

📌 Overview

This project provides two implementations of a Java-based system monitoring service:

1️⃣ Basic Monitoring – Uses Java's built-in ManagementFactory and Runtime (no external dependencies).2️⃣ Advanced Monitoring (OSHI-based) – Uses OSHI (oshi-core) for detailed CPU, memory, disk, and network monitoring.


🚀 Features

✅ Monitors CPU usage, memory, disk space, and network activity✅ Cross-platform support (Windows, Linux, macOS)✅ Two implementations: one with OSHI and one without✅ Console-based monitoring loop that runs periodically✅ Logging with java.util.logging.Logger


🛠️ Installation & Running

1️⃣ Clone the Repository
```
git clone https://github.com/devjefster/monitoring-java.git
cd monitoring-java
```

2️⃣ Build the Project (Using Maven)
```
mvn clean install
```

3️⃣ Run the Monitoring Service

Without OSHI (Basic Implementation)
```
java -jar target/java-monitoring-1.0-SNAPSHOT.jar
```

With OSHI (Advanced Implementation)

Switch to the OSHI-based implementation and run:
```
java -jar target/java-monitoring-1.0-SNAPSHOT.jar --use-oshi
```

📖 Articles & Documentation

Learn more about the implementation details in the Medium articles:

📌 Basic Java System Monitoring (No External Libraries)🔗 Read on Medium

📌 Advanced Java System Monitoring Using OSHI🔗 Read on Medium

🔹 Contribution

Feel free to fork this repository and submit pull requests for improvements! 🚀


📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

