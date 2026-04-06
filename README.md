# Gestor de Tareas – Backend (Spring Boot + JWT)

Backend desarrollado en **Spring Boot** para gestionar usuarios y tareas con autenticación mediante **JWT**.  
Incluye registro, login, seguridad, CRUD de tareas y endpoint `/auth/me` para obtener el usuario autenticado.

---

## 🚀 Tecnologías utilizadas

- Java 21
- Spring Boot 
- Spring Security 
- JWT (JSON Web Token)
- JPA / Hibernate
- PostgreSQL
- Maven

---

## 📦 Requisitos

- Java 17+
- Maven
- PostgreSQL
- IntelliJ IDEA (opcional cualquier IDE)

---

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestor_tareas
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

▶️ Ejecutar el proyecto

mvn spring-boot:run

El backend se inicia en: http://localhost:8080

🔐 Autenticación JWT

El backend usa JWT para proteger rutas.

Flujo:

   1.Registrar usuario → /auth/register

   2.Login → /auth/login → devuelve token

   3.Enviar token en cada petición protegida: 
   Authorization: Bearer TU_TOKEN

   4.Obtener usuario autenticado → /auth/me

📚 Endpoints principales

 🔸 Autenticación
  Método	Endpoint	Descripción
  POST	/auth/register	Registrar usuario
  POST	/auth/login	Iniciar sesión y obtener token
  GET	/auth/me	Obtener usuario autenticado

 🔸 Tareas
  Método	Endpoint	Descripción
  GET	/tasks/user/{id}	Obtener tareas del usuario
  POST	/tasks/create/{id}	Crear tarea para un usuario
  PUT	/tasks/update/{id}	Actualizar tarea
  DELETE	/tasks/delete/{id}	Eliminar tarea

📁 Estructura del proyecto

  src/main/java/com/cristian/gestor_tareas
  │
  
  ├── controller
  
  ├── dto
  
  ├── model
  
  ├── repository
  
  ├── security
  
  ├── service
  
  └── GestorTareasApplication.java

👤 Autor

 Cristian Alhambra — Full Stack Developer
 
 Proyecto backend con Spring Boot + JWT. Proyecto libre.

