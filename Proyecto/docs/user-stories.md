# Historias de Usuario

## US-01 — Crear cuenta de usuario
**Épica:** 1. Gestión de cuenta  
**Feature:** 1.1 Crear cuenta de usuario
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero crear una cuenta de usuario para poder acceder a la plataforma y realizar pedidos.

### Criterios de aceptación
- El formulario solicita nombre, correo, teléfono y contraseña.
- El sistema valida que el correo no esté registrado previamente.
- El sistema valida el formato del correo y una contraseña segura.
- Al finalizar, el usuario recibe confirmación de registro exitoso.

**Prioridad:** Complejidad Media  
**Estimación:** 6.5 puntos

---

## US-02 — Iniciar sesión
**Épica:** 1. Gestión de cuenta  
**Feature:** 1.2 Iniciar sesión
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero iniciar sesión con mis credenciales para acceder a mi cuenta.

### Criterios de aceptación
- El sistema valida usuario y contraseña.
- Se muestra un mensaje de error si las credenciales son incorrectas.
- Al iniciar sesión correctamente, el usuario es redirigido a la pantalla principal.
- Existe opción de recuperar contraseña.

**Prioridad:** Complejo  
**Estimación:** 10.5 puntos

---

## US-03 — Ver listado de restaurantes cercanos
**Épica:** 2. Descubrimiento y pedido  
**Feature:** 2.1 Ver listado de restaurantes cercanos
**Responsable:** Sebastian Bravo Yarce

Como cliente quiero ver el listado de restaurantes cercanos para elegir dónde realizar mi pedido.

### Criterios de aceptación
- El sistema muestra los restaurantes ordenados por cercanía.
- Cada restaurante muestra nombre, imagen y calificación promedio.
- El usuario puede filtrar o buscar por nombre o categoría.

**Prioridad:** Poco complejo  
**Estimación:** 4 puntos

---

## US-04 — Ver menú/carta de uno de los restaurantes elegidos
**Épica:** 2. Descubrimiento y pedido  
**Feature:** 2.2 Ver menú/carta de uno de los restaurantes elegidos
**Responsable:** Sebastian Bravo Yarce

Como cliente quiero ver el menú de un restaurante para conocer los productos disponibles.

### Criterios de aceptación
- El menú muestra nombre, precio y descripción de cada producto.
- Los productos están agrupados por categoría.
- Se muestra si un producto no está disponible.

**Prioridad:** Poco complejo 
**Estimación:** 4 puntos

---

## US-05 — Agregar los productos al carrito de compras
**Épica:** 2. Descubrimiento y pedido  
**Feature:** 2.3 Agregar los productos al carrito de compras
**Responsable:** Luis Miguel Alvarez

Como cliente quiero agregar productos a mi carrito para armar mi pedido antes de confirmarlo.

### Criterios de aceptación
- El usuario puede agregar y quitar productos del carrito.
- El usuario puede modificar la cantidad de cada producto.
- El carrito muestra el subtotal actualizado en tiempo real.

**Prioridad:** Complejo  
**Estimación:** 10.5 puntos

---

## US-06 — Confirmar el pedido
**Épica:** 2. Descubrimiento y pedido  
**Feature:** 2.4 Confirmar el pedido
**Responsable:** Luis Miguel Alvarez

Como cliente quiero confirmar mi pedido para enviarlo al restaurante.

### Criterios de aceptación
- El sistema muestra un resumen del pedido antes de confirmar.
- El pedido queda asociado al restaurante y al usuario.
- El estado del pedido cambia a 'Confirmado' y se notifica al comercio.

**Prioridad:** Complidad Media  
**Estimación:** 5 puntos

---

## US-07 — Genera factura
**Épica:** 3. Pagos y facturación  
**Feature:** 3.1 Genera factura
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero recibir una factura de mi pedido para tener un comprobante de la compra.

### Criterios de aceptación
- La factura incluye productos, cantidades, precios y total.
- La factura queda disponible en el historial de pedidos.
- La factura se genera automáticamente al confirmar el pago.

**Prioridad:** Poco complejo  
**Estimación:** 3 puntos

---

## US-08 — Selección método de pago Efectivo
**Épica:** 3. Pagos y facturación  
**Feature:** 3.2 Selección método de pago Efectivo
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero pagar en efectivo para cancelar mi pedido al momento de recogerlo.

### Criterios de aceptación
- El usuario puede seleccionar 'Efectivo' como método de pago.
- El pedido queda marcado como 'Pendiente de pago' hasta la recogida.
- El comercio puede confirmar el pago al momento de la entrega.

**Prioridad:** Sencillo  
**Estimación:** 2 puntos

---

## US-09 — Selección método de pago Tarjeta
**Épica:** 3. Pagos y facturación  
**Feature:** 3.3 Selección método de pago Tarjeta
**Responsable:** Sebastian Bravo Yarce

Como cliente quiero pagar con tarjeta para cancelar mi pedido de forma electrónica.

### Criterios de aceptación
- El usuario puede ingresar los datos de su tarjeta de forma segura.
- El sistema valida la transacción antes de confirmar el pedido.
- El usuario recibe confirmación del pago realizado.

**Prioridad:** Muy Complejo 
**Estimación:** 13 puntos

---

## US-10 — Revisión de tiempo estimado de recogida
**Épica:** 4. Seguimiento de compra  
**Feature:** 4.1 Revisión de tiempo estimado de recogida
**Responsable:** Sebastian Bravo Yarce

Como cliente quiero ver el tiempo estimado de recogida para planificar mi llegada al restaurante.

### Criterios de aceptación
- El sistema muestra un tiempo estimado luego de confirmar el pedido.
- El tiempo estimado se actualiza si el comercio lo modifica.
- El usuario recibe una notificación cuando el pedido está listo.

**Prioridad:** Sencillo  
**Estimación:** 2.5 puntos

---

## US-11 — Recibe código QR de la compra
**Épica:** 4. Seguimiento de compra  
**Feature:** 4.2 Recibe código QR de la compra
**Responsable:** Luis Miguel Alvarez

Como cliente quiero recibir un código QR de mi compra para poder reclamar mi pedido en el restaurante.

### Criterios de aceptación
- El código QR se genera automáticamente al confirmar el pedido.
- El código QR es único por pedido.
- El usuario puede visualizar el QR desde el detalle del pedido.

**Prioridad:** Poco complejo  
**Estimación:** 4 puntos

---

## US-12 — Ver historial de pedidos
**Épica:** 4. Seguimiento de compra  
**Feature:** 4.3 Ver historial de pedidos
**Responsable:** Luis Miguel Alvarez

Como cliente quiero ver mi historial de pedidos para consultar compras anteriores.

### Criterios de aceptación
- El historial muestra fecha, restaurante, productos y total de cada pedido.
- El usuario puede acceder a la factura de cada pedido pasado.
- El historial se ordena del más reciente al más antiguo.

**Prioridad:** Poco complejo
**Estimación:** 4 puntos

---

## US-13 — Editar datos del perfil
**Épica:** 1. Gestión de cuenta  
**Feature:** 1.3 Editar datos del perfil
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero editar los datos de mi perfil para mantener mi información actualizada.

### Criterios de aceptación
- El usuario puede modificar nombre, teléfono, dirección y foto.
- El sistema valida los campos antes de guardar los cambios.
- El usuario recibe confirmación de que los cambios se guardaron.

**Prioridad:** Complejo  
**Estimación:** 8 puntos

---

## US-14 — Puntuación y comentarios de comercio según su experiencia
**Épica:** 5. Experiencia del usuario  
**Feature:** 5.1 Puntuación y comentarios de comercio según su experiencia
**Responsable:** Juan Camilo Chica Arango

Como cliente quiero calificar y comentar mi experiencia con un comercio para compartir mi opinión con otros usuarios.

### Criterios de aceptación
- El usuario puede calificar de 1 a 5 estrellas y dejar un comentario.
- Solo se permite calificar pedidos ya entregados.
- La calificación se refleja en el promedio del comercio.

**Prioridad:** Sencillo  
**Estimación:** 2.5 puntos

---

## US-15 — Iniciar sesión como comerciante
**Épica:** 6. Gestión de cuenta comerciante  
**Feature:** 6.1 Iniciar sesión como comerciante
**Responsable:** Sebastian Bravo Yarce

Como comerciante quiero iniciar sesión para acceder a la administración de mi negocio.

### Criterios de aceptación
- El sistema valida usuario y contraseña del comerciante.
- El acceso está separado del inicio de sesión de clientes.
- Se muestra un mensaje de error si las credenciales son incorrectas.

**Prioridad:** Complejo  
**Estimación:** 8 puntos

---

## US-16 — Administración del espacio virtual (Perfil)
**Épica:** 6. Gestión de cuenta comerciante  
**Feature:** 6.2 Administración del espacio virtual (Perfil)
**Responsable:** Sebastian Bravo Yarce

Como comerciante quiero administrar el perfil de mi negocio para mantener actualizada su información.

### Criterios de aceptación
- El comerciante puede editar nombre, dirección, horario y logo del negocio.
- Los cambios se reflejan inmediatamente en el perfil visible a los clientes.
- El sistema valida los campos obligatorios antes de guardar.

**Prioridad:** Complejidad Media  
**Estimación:** 5 puntos

---

## US-17 — Editar información de productos del menú
**Épica:** 7. Gestión de menú  
**Feature:** 7.1 Editar información de productos del menú
**Responsable:** Luis Miguel Alvarez

Como comerciante quiero editar la información de mis productos para mantener el menú actualizado.

### Criterios de aceptación
- El comerciante puede editar nombre, descripción, precio e imagen de cada producto.
- El sistema valida que el precio sea un valor numérico positivo.
- Los cambios se reflejan de inmediato en el menú visible al cliente.

**Prioridad:** Complejidad Media  
**Estimación:** 5 puntos

---

## US-18 — Actualización de datos de productos
**Épica:** 7. Gestión de menú  
**Feature:** 7.2 Actualización de datos de productos
**Responsable:** Luis Miguel Alvarez

Como comerciante quiero actualizar la disponibilidad de mis productos para evitar que se pidan productos agotados.

### Criterios de aceptación
- El comerciante puede marcar un producto como disponible o agotado.
- Un producto agotado no puede agregarse al carrito por el cliente.
- El cambio de disponibilidad se refleja en tiempo real.

**Prioridad:** Poco Complejo  
**Estimación:** 4 puntos

---

## US-19 — Ver pedidos entrantes en tiempo real
**Épica:** 8. Gestión de pedidos  
**Feature:** 8.1 Ver pedidos entrantes en tiempo real
**Responsable:** Juan Camilo Chica Arango

Como comerciante quiero ver los pedidos entrantes en tiempo real para atenderlos sin demoras.

### Criterios de aceptación
- Los nuevos pedidos aparecen automáticamente sin recargar la página.
- Cada pedido muestra los productos, cantidades y método de pago.
- El comerciante puede aceptar o rechazar un pedido entrante.

**Prioridad:** Muy Complejo  
**Estimación:** 13 puntos

---

## US-20 — Estimación de tiempos según demanda
**Épica:** 8. Gestión de pedidos  
**Feature:** 8.2 Estimación de tiempos según demanda
**Responsable:** Juan Camilo Chica Arango

Como comerciante quiero estimar el tiempo de preparación según la demanda para informar correctamente al cliente.

### Criterios de aceptación
- El sistema sugiere un tiempo estimado según los pedidos activos.
- El comerciante puede ajustar manualmente el tiempo estimado.
- El tiempo estimado se envía al cliente asociado al pedido.

**Prioridad:** Complejo 
**Estimación:** 10.5 puntos

---

## US-21 — Marcación de pedidos listos
**Épica:** 8. Gestión de pedidos  
**Feature:** 8.3 Marcación de pedidos listos
**Responsable:** Sebastian Bravo Yarce

Como comerciante quiero marcar un pedido como listo para notificar al cliente que puede recogerlo.

### Criterios de aceptación
- El comerciante puede cambiar el estado del pedido a 'Listo'.
- El cliente recibe una notificación al cambiar el estado.
- El estado del pedido queda visible en el historial.

**Prioridad:** Poco Complejo  
**Estimación:** 4 puntos

---

## US-22 — Confirmar método de pago
**Épica:** 8. Gestión de pedidos  
**Feature:** 8.4 Confirmar método de pago
**Responsable:** Sebastian Bravo Yarce

Como comerciante quiero confirmar el método de pago de un pedido para validar que la transacción se completó correctamente.

### Criterios de aceptación
- El comerciante puede visualizar el método de pago seleccionado por el cliente.
- Para pagos en efectivo, el comerciante puede confirmar el pago recibido.
- El pedido queda marcado como 'Pagado' tras la confirmación.

**Prioridad:** Poco complejo 
**Estimación:** 3 puntos

---

## US-23 — Escaneo QR
**Épica:** 8. Gestión de pedidos  
**Feature:** 8.5 Escaneo QR
**Responsable:** Luis Miguel Alvarez

Como comerciante quiero escanear el código QR del cliente para validar y entregar su pedido.

### Criterios de aceptación
- El sistema valida que el QR corresponda a un pedido activo.
- Al escanear, el pedido cambia su estado a 'Entregado'.
- Se muestra un mensaje de error si el código QR no es válido.

**Prioridad:** Complejidad Media  
**Estimación:** 5 puntos

---

## US-24 — Puntuación y comentarios de usuario según su historial de pago
**Épica:** 9. Evaluación y reportes  
**Feature:** 9.1 Puntuación y comentarios de usuario según su historial de pago
**Responsable:** Luis Miguel Alvarez

Como comerciante quiero calificar a un cliente según su historial de pago para identificar clientes confiables.

### Criterios de aceptación
- El comerciante puede calificar solo pedidos ya finalizados.
- La calificación se registra en el historial del cliente.
- El comerciante puede dejar un comentario opcional.

**Prioridad:** Poco complejo  
**Estimación:** 3 puntos

---

## US-25 — Reporte de datos
**Épica:** 9. Evaluación y reportes  
**Feature:** 9.2 Reporte de datos
**Responsable:** Juan Camilo Chica Arango

Como comerciante quiero generar reportes de mis ventas para conocer el desempeño de mi negocio.

### Criterios de aceptación
- El reporte muestra ventas totales por periodo seleccionado.
- El reporte incluye los productos más vendidos.
- El comerciante puede exportar el reporte.

**Prioridad:** Complejidad Media  
**Estimación:** 5 puntos

---

## US-26 — Inicio de sesión como administrador
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.1 Inicio de sesión como administrador
**Responsable:** Juan Camilo Chica Arango

Como administrador quiero iniciar sesión para acceder al panel de gestión de la plataforma.

### Criterios de aceptación
- El sistema valida usuario y contraseña del administrador.
- El acceso administrativo está separado del acceso de clientes y comercios.
- Se muestra un mensaje de error si las credenciales son incorrectas.

**Prioridad:** Complejo  
**Estimación:** 8 puntos

---

## US-27 — Revisión de requisitos mínimos para afiliación
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.2 Revisión de requisitos mínimos para afiliación
**Responsable:** Sebastian Bravo Yarce

Como administrador quiero revisar los requisitos mínimos de un comercio para aprobar o rechazar su afiliación.

### Criterios de aceptación
- El administrador puede visualizar la documentación enviada por el comercio.
- El administrador puede aprobar o rechazar la solicitud con un comentario.
- El comercio recibe notificación del resultado de la revisión.

**Prioridad:** Poco Complejo  
**Estimación:** 3 puntos

---

## US-28 — Creación de perfil del comercio / usuario
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.3 Creación de perfil del comercio / usuario
**Responsable:** Sebastian Bravo Yarce

Como administrador quiero crear el perfil de un comercio o usuario para habilitar su acceso a la plataforma.

### Criterios de aceptación
- El administrador puede registrar los datos básicos del comercio o usuario.
- El sistema valida que no exista un perfil duplicado.
- El nuevo perfil queda disponible en el listado de administración.

**Prioridad:** Muy complejo 
**Estimación:** 13 puntos

---

## US-29 — Activación de perfiles de comercio / usuario
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.4 Activación de perfiles de comercio / usuario
**Responsable:** Luis Miguel Alvarez

Como administrador quiero activar perfiles de comercio o usuario para habilitar su uso de la plataforma.

### Criterios de aceptación
- El administrador puede cambiar el estado de un perfil a 'Activo'.
- El perfil activado puede iniciar sesión con normalidad.
- Se registra la fecha y el administrador que realizó la activación.

**Prioridad:** Poco complejo 
**Estimación:** 3 puntos

---

## US-30 — Desactivación de perfiles de comercio / usuario
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.5 Desactivación de perfiles de comercio / usuario
**Responsable:** Luis Miguel Alvarez

Como administrador quiero desactivar perfiles de comercio o usuario para restringir su acceso cuando sea necesario.

### Criterios de aceptación
- El administrador puede cambiar el estado de un perfil a 'Inactivo'.
- Un perfil desactivado no puede iniciar sesión.
- Se registra la fecha y el motivo de la desactivación.

**Prioridad:** Poco complejo  
**Estimación:** 3 puntos

---

## US-31 — Ver reportes de actividad de plataforma
**Épica:** 10. Gestión de la plataforma  
**Feature:** 10.6 Ver reportes de actividad de plataforma
**Responsable:** Juan Camilo Chica Arango

Como administrador quiero ver reportes de actividad de la plataforma para supervisar su funcionamiento general.

### Criterios de aceptación
- El reporte muestra número de usuarios, comercios y pedidos activos.
- El administrador puede filtrar el reporte por fecha.
- El reporte puede exportarse para su análisis.

**Prioridad:** Poco complejo  
**Estimación:** 3 puntos

---
