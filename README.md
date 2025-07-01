# Servicio Api Plataforma Vida Grupo

Api backend plataforma vida grupo para cotizadores web v2

### Requisitos

```
# java version 21
# IDE para correr aplicativos en spring boot
# Gradle 8
```

### Variables de Entorno

```
Infrastructure
- HTTP_PORT: Esta variable de entorno especifica el puerto en el que se ejecutará el servidor, en openshift se configura el mismo por el Dockerfile
- CONTEXT_PATH: Contexto base de la ruta de la api
- BASIC_AUTH_USER: Usuario para la autenticación básica
- BASIC_AUTH_PASS: Contraseña del usuario para la autenticación básica
- MONGODB_URI: String de conexion para base de datos de mongo CotWeb-v2-persistencia
- API_CATALOG_URL: URL base del servicio de catalogos para vida grupo (coberturas, giros, planes), ej: https://api-dev.segurossura.com.mx/api-catalogs/v2
- API_FOLIO_URL: URL base del servicio flodeador de cotizadores, ej: https://api-dev.segurossura.com.mx/api-sequence-folio/v2
- API_COMMISSION_URL: URL base del servicio de consulta de comisiones de agentes, ej: https://api-dev.segurossura.com.mx/api-bonuses-commissions/v1
- MSAL_APIKEY: X API KEY del producto vida-grupo para solicitudes a microservicios por APM
- AGENTPORTAL_URL: Ruta del portal de agentes
- AGENTPORTAL_JWT_SECRETKEY: Clave secreta de encriptación de JWT desde portal agentes, la misma se encuentra en la variables de portal-agentes microservicio/backend variable SECRET
- AGENTPORTAL_RECOVER_SECRETKEY: Clave secreta de encriptación de data dentro del JWT desde recuperación de tramite de portal agentes, la misma se encuentra en la variable portal-agentes front variable REACT_APP_RECOTIZADORES_V2_KEY
- API_PRICING_URL: URL base del servicio de calculadora para vida grupo, ej: https://api-dev.segurossura.com.mx/api-pricing-life-group/v2
- API_PRICING_VERSION: Version del servicio de calculadora para vida grupo, ej: v1
- API_MUI_JASPER_URL: URL base del servicio de generar pdf con jasper json, ej: http://mui-proxy.back-subdomain-documentos-dev.svc:8080
- API_STORAGE_URL: URL base del servicio de guardar los pdfs en storage para generar ruta publica de descarga, ej: https://api-dev.segurossura.com.mx/api-filestorage-public/v1
- API_VALIDATION_URL: URL base del servicio de validaciones (rfc), ej: https://api-dev.segurossura.com.mx/api-validations/v1
- API_CATALOGUE_URL: URL base del servicio de catalogos (busqueda rfc), ej: https://api-dev.segurossura.com.mx/api-catalogues/v1
- API_ISSUE_URL: URL base del servicio de conectar a Alea para llegar a la emision, ej: https://api-dev.segurossura.com.mx/api-grouplife-issue/v1
- API_ISSUE_DIRECT: URL base del servicio de conectar a Alea, se usa ruta interna que no se tiene token del front por ser tarea programada, ej: http://ms-vidagrupo-emite-back-subdomain-aleatools-dev.apps.usjof733.eastus.aroapp.io/api-grouplife-issue/v1
- API_ISSUE_USER: Usuario para la autenticación básica
- API_ISSUE_PASS: Contraseña para la autenticación básica
- API_PAYMENT_DIRECT: URL base del servicio de generar liga de pasarela, se usa ruta interna que no se tiene token del front por ser tarea programada, ej: http://ws-pasarela-backend-pasarela-de-pago-dev.apps.usjof733.eastus.aroapp.io/api-paymentgateway
- API_PAYMENT_USER: Usuario para la autenticación básica
- API_PAYMENT_PASS: Contraseña para la autenticación básica
- API_PRINT_URL: URL del endpoint del servicio de impresion PUD, ej: http://impresion-back-subdomain-plataformadigital-dev.apps.usjof733.eastus.aroapp.io/api/impresion/impresiones
- API_MAILSENDGRID_DIRECT: URL del servicio de envio de correos por sendgrid, http://ms-mail-service-sendgrid-transversal-dev.apps.usjof733.eastus.aroapp.io/api/send/mail
- API_MAILSENDGRID_USER: Usuario para la autenticación básica
- API_MAILSENDGRID_PASS: Contraseña para la autenticación básica
- API_MAILSENDGRID_FROM: Correo remitente
- API_MAILSENDGRID_APIKEY: Apikey de sendgrid
- API_MAILSENDGRID_TEMPLATE_NOTIFICATION: Identificador de plantilla para correo de notificaciones
- API_MAIL_POLICY_URL: URL del servicio de envio de correo de polizas emitidas, API_MAIL_POLICY_URL: http://poliza-correo.back-subdomain-aleatools-dev.svc8080/correo

```

### Ejecutar entorno local

```
# install dependencies
gradle

# run on port HTTP_PORT
mediante el IDE utiliza correr el aplicativo como SpringBoot
```
