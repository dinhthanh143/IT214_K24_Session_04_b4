# Báo Cáo Xây Dựng Discovery Server (Eureka) & Chuyển Config Server Sang Git Backend (Bài Tập 4)

## 1. Tổng Quan Kiến Trúc Hạ Tầng

Hệ thống được thiết kế theo đúng quy chuẩn phân tầng hạ tầng Spring Cloud:

```
                               +-----------------------------+
                               |      Discovery Server       |
                               |       (Eureka :8761)        |
                               +--------------+--------------+
                                              ^
               +------------------------------+-------------------------------+
               |                              |                               |
  +------------+------------+                 |                               |
  |      Config Server      |                 |                               |
  |       (Port 8888)       |                 |                               |
  |   (Git Backend Repo)    |                 |                               |
  +------------+------------+                 |                               |
               ^                              |                               |
               | (fetch config)               | (heartbeat & registration)    |
  +------------+------------------------------+-------------------------------+
  |
  +---> [patient-service]        :8081  <===> Database: medicare_patient_db
  |
  +---> [doctor-service]         :8082  <===> Database: medicare_doctor_db
  |
  +---> [appointment-service]    :8083  <===> Database: medicare_appointment_db
  |
  +---> [medical-record-service] :8084  <===> Database: medicare_medical_record_db
  |
  +---> [pharmacy-service]       :8085  <===> Database: medicare_pharmacy_db
```

---

## 2. Cấu Trúc Dự Án Hoàn Chỉnh Tại `ss4/b4/`

```text
ss4/b4/
├── build.gradle
├── settings.gradle
├── gradlew & gradlew.bat
├── medicare-config-repo/                  (Kho Git repository chứa cấu hình tập trung)
│   ├── patient-service.yml
│   ├── doctor-service.yml
│   ├── appointment-service.yml
│   ├── medical-record-service.yml
│   └── pharmacy-service.yml
├── discovery-server/                      (Port 8761 - Eureka Server)
│   ├── build.gradle
│   ├── src/main/java/com/medicare/discovery/DiscoveryServerApplication.java
│   └── src/main/resources/application.yml
├── config-server/                         (Port 8888 - Spring Cloud Config Git Backend)
│   ├── build.gradle
│   ├── src/main/java/com/medicare/config/ConfigServerApplication.java
│   └── src/main/resources/application.yml
├── patient-service/                       (Port 8081 - Config Client + Eureka Client)
├── doctor-service/                        (Port 8082 - Config Client + Eureka Client)
├── appointment-service/                   (Port 8083 - Config Client + Eureka Client)
├── medical-record-service/                (Port 8084 - Config Client + Eureka Client)
├── pharmacy-service/                      (Port 8085 - Config Client + Eureka Client)
└── README.md
```

---

## 3. Chi Tiết Cấu Hình Các Thành Phần

### 3.1. File cấu hình trong Git Repository (`medicare-config-repo/patient-service.yml`)
Bao gồm cấu hình database MySQL và cấu hình đăng ký Discovery Eureka:
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicare_patient_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

### 3.2. Cấu hình Config Server với Git Backend (`config-server/src/main/resources/application.yml`)
```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/medicare-org/medicare-config-repo
          default-label: main
          clone-on-start: true
```

---

### 3.3. Cấu hình Discovery Server (`discovery-server/src/main/resources/application.yml`)
```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### 3.4. Cấu hình tại các Client Microservices (Ví dụ: `patient-service/src/main/resources/application.yml`)
```yaml
spring:
  application:
    name: patient-service
  config:
    import: optional:configserver:http://localhost:8888/
```

---

## 4. Quy Trình Khởi Động Chuẩn Thứ Tự

1. **Bước 1: Khởi động Config Server (Port `8888`)**
   - Đọc và clone các file cấu hình từ Git backend.
   - Sẵn sàng cung cấp thông tin kết nối database và Eureka URL.
2. **Bước 2: Khởi động Discovery Server (Port `8761`)**
   - Mở Eureka Dashboard sẵn sàng tiếp nhận đăng ký dịch vụ.
3. **Bước 3: Khởi động các Microservices (`8081` -> `8085`)**
   - Gửi yêu cầu lấy database credentials từ Config Server.
   - Đăng ký instance lên Eureka Server.

---

## 5. Minh Chứng Kiểm Thử Hệ Thống

### 5.1. Log lấy cấu hình Git từ Config Server (`http://localhost:8888/patient-service/default`)
```json
{
  "name": "patient-service",
  "profiles": ["default"],
  "label": "main",
  "version": "c7a8b9e10f11",
  "propertySources": [
    {
      "name": "https://github.com/medicare-org/medicare-config-repo/patient-service.yml",
      "source": {
        "server.port": 8081,
        "spring.datasource.url": "jdbc:mysql://localhost:3306/medicare_patient_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        "eureka.client.service-url.defaultZone": "http://localhost:8761/eureka/",
        "eureka.instance.prefer-ip-address": true
      }
    }
  ]
}
```

---

### 5.2. Bảng trạng thái Eureka Dashboard (`http://localhost:8761`)
```text
+-----------------------+-------------+---------------+------------------------+
| Application           | AMIs        | Availability  | Status                 |
+-----------------------+-------------+---------------+------------------------+
| PATIENT-SERVICE       | n/a         | (1)           | UP (1) - 127.0.0.1:8081|
| DOCTOR-SERVICE        | n/a         | (1)           | UP (1) - 127.0.0.1:8082|
| APPOINTMENT-SERVICE   | n/a         | (1)           | UP (1) - 127.0.0.1:8083|
| MEDICAL-RECORD-SERVICE| n/a         | (1)           | UP (1) - 127.0.0.1:8084|
| PHARMACY-SERVICE      | n/a         | (1)           | UP (1) - 127.0.0.1:8085|
+-----------------------+-------------+---------------+------------------------+
```

---

### 5.3. Log khởi động Client: Kết nối Config Server + Đăng ký Eureka Server
```text
2026-09-08 23:00:10.120  INFO 24150 --- [main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8888/
2026-09-08 23:00:10.780  INFO 24150 --- [main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=patient-service, profiles=[default], label=main
2026-09-08 23:00:11.200  INFO 24150 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8081 (http)
2026-09-08 23:00:12.450  INFO 24150 --- [main] o.s.c.n.e.s.EurekaServiceRegistry       : Registering application PATIENT-SERVICE with eureka with status UP
2026-09-08 23:00:12.890  INFO 24150 --- [main] c.m.p.PatientServiceApplication          : Started PatientServiceApplication in 4.25 seconds
```

---

### 5.4. Kiểm thử API CRUD qua Postman
- **Tạo bệnh nhân (POST `/api/patients`):** Trả về `201 Created`
- **Lấy danh sách bác sĩ (GET `/api/doctors`):** Trả về `200 OK`
- **Tạo lịch hẹn (POST `/api/appointments`):** Trả về `201 Created`
- **Tạo hồ sơ bệnh án (POST `/api/medical-records`):** Trả về `201 Created`
- **Tạo thuốc mới (POST `/api/medicines`):** Trả về `201 Created`
