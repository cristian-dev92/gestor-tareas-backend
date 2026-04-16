# Gestor de Tareas – Backend (Spring Boot + JWT)

Backend robusto desarrollado con **Java 21** y **Spring Boot** para la gestión de tareas.  
Implementa una arquitectura de seguridad basada en **JWT** (JSON Web Tokens) y persistencia en **PostgreSQL**.

---

## 🚀 Stack Tecnológico

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.x
* **Seguridad:** Spring Security & JWT
* **Persistencia:** Spring Data JPA + Hibernate
* **Base de Datos:** PostgreSQL
* **Gestión de Dependencias:** Maven

---

## 🔐 Seguridad y Autenticación

El sistema utiliza un flujo de autenticación stateless mediante JWT:

1.  **Registro:** El usuario se crea en `/auth/register`.
2.  **Login:** El usuario se autentica en `/auth/login` y recibe un Bearer Token.
3.  **Protección:** Las rutas de tareas requieren el header: `Authorization: Bearer <TOKEN_AQUÍ>`
4.  **Sesión:** Endpoint `/auth/me` para validar y recuperar el perfil del usuario actual.

---

## ⚙️ Configuración

Antes de ejecutar, asegúrate de configurar tu base de datos en `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=${JWT_SECRET}
cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

📚 Documentación de la API

🔸 Autenticación

| Método |    Endpoint    |              Descripción               |
| :---: |:--------------:|:--------------------------------------:| 
| POST | /auth/register |       Registrar un nuevo usuario       |
| POST |  /auth/login   |    Login y obtención del Token JWT     |
| GET |     /tasks     | Obtener perfil del usuario autenticado |

 🔸 Gestión de Tareas

| Método |       Endpoint       |                Descripción                |
|:------:|:--------------------:|:-----------------------------------------:| 
|  GET   |  /tasks/user/{id}	   |   Lista todas las tareas de un usuario    |   
|  POST  | /tasks/create/{id}   | 	Crea una nueva tarea asignada al usuario | 
|  PUT	  | /tasks/update/{id}	  |  Actualiza datos de una tarea existente   | 
| DELETE | /tasks/delete/{id}	  |   Elimina una tarea de forma permanente   |

📁 Estructura del proyecto

  src/main/java/com/cristian/gestor_tareas
  │

  ├—— controller # (Capa de Exposición): Contiene los controladores REST. Define los puntos de entrada (endpoints) y gestiona las peticiones HTTP.

  ├—— dto # (Data Transfer Objects): Objetos utilizados para enviar y recibir datos desde la API, evitando exponer directamente las entidades de la base de datos y mejorando la seguridad.
  
  ├—— model # (Capa de Datos): Contiene las entidades JPA que representan las tablas de la base de datos (Usuario, Tarea).
  
  ├—— repository # (Capa de Persistencia): Interfaces que extienden de `JpaRepository`. Se encargan de realizar las consultas a la base de datos PostgreSQL.
  
  ├—— security # (Capa de Seguridad): Configuración de Spring Security, filtros de JWT y lógica de cifrado de contraseñas.
  
  ├—— service # (Capa de Negocio): Aquí reside la lógica principal. Gestiona las validaciones, el procesamiento de datos y la comunicación entre controladores y repositorios.
  
  └── GestorTareasApplication.java

▶️ Ejecución

Clona el proyecto y ejecuta el siguiente comando en la raíz:

mvn spring-boot:run

La API estará disponible en: http://localhost:8080

👤 Autor

 Cristian Alhambra - Full Stack Developer

