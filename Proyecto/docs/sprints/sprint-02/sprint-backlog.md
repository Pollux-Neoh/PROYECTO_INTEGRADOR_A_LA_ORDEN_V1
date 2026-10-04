# Sprint 2 — Sprint Backlog

## Historias seleccionadas
- US-05 — Agregar los productor al carrito compras
- US-06 — Confirmar el pedido
- US-07 — Genera factura
- US-08 — Selección método de pago Efectivo

---

## Tareas Back-end (Java)

### US-05 — Agregar los productos al carrito de compras
- [ ] Crear clase `Carrito` / `ItemCarrito`(atributos, constructor, getters/setters)
- [ ] Implementar lógica para agregar y quitar productos del carrito
- [ ] Implementar lógica para modificar la cantidad de cada producto
- [ ] Implementar cálculo de subtotal actualizado en tiempo real

### US-06 — Confirmar el pedido
- [ ] Crear/ajustar clase `Pedido` (relación con Cliente, Restaurante y Carrito)
- [ ] Implementar lógica para generar el resumen del pedido antes de confirmar
- [ ] Implementar cambio de estado del pedido a `Confirmado` y notificación al comercio

### US-07 — Genera factura
- [ ] Crear clase `Factura` (productos, cantidades, precios, total)
- [ ] Implementar generación automática de factura al corfirmas el pago
- [ ] Implementar servicio de historial de pedidos/facturas por usuario

### US-08 — Selección método de pago Efectivo
- [ ] Crear/ajustar clase `Pago` con atributo método de pago
- [ ] Implementar lógica para marcar el pedido como `Pendiente de pago`
- [ ] Implementar confirmación de pago por parte del comercio al momento de la entrega

---

## Tareas Front-end (JavaScript)

### US-05 — Agregar los productos al carrito de compras
- [ ] Diseñar vista/componente del carrito de compras
- [ ] Implementar botones de agregar/quitar producto y selector de cantidad
- [ ] Conectar carrito con el back-end y mostrar subtotal en tiempo real

### US-06 — Confirmar el pedido
- [ ] Diseñar vista de resimen del pedido antes de confirmar
- [ ] Implementar botón de confirmación del pedido
- [ ] Conectar vista con el back-end y mostrar el estado del pedido

### US-07 — Genera factura
- [ ] Diseñar vista de factura/comprabante
- [ ] Diseñar vista de historial de pedidos con acceso a facturas
- [ ] Conectar vistas con el servicio de facturación

### US-08 — Selección método de pago Efectivo
- [ ] Diseñar vista de selección de método de pago (Efectivo)
- [ ] Conectar selección con el back-end
- [ ] Mostrar estado `Pendiente de pago` en la vista del cliente

---

## Tareas conjuntas (Back + Front)
- [ ] Probar agregar/quitar productos del carrito (US-05)
- [ ] Probar confirmación del pedido (US-06)
- [ ] Probar generación de factura (US-07)
- [ ] Probar selección de pago en efectivo (US-08)

## Sprint 1 Info
- **Duración:** 2 semanas
- **Historias:** 4 (US-05, US-06, US-07, US-08)
- **Puntos:** 20.5 (aprox.)
