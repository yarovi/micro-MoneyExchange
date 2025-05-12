# Getting Started

### Reference Documentation

Empty

## 🔄 Ejemplo de Conversión de Divisas

Este endpoint permite realizar la conversión de una cantidad entre dos monedas. http://localhost:8080

- **Método:** `POST`
- **Endpoint:** `/api/exchange/convert`

### 📥 Request Body

```json
{
  "amount": 150,
  "sourceCurrency": "USD",
  "targetCurrency": "EUR"
}
```
### 📥 Response
```json
{
  "sourceAmount": 150.0,
  "convertedAmount": 134.9865,
  "sourceCurrency": "USD",
  "targetCurrency": "EUR",
  "exchangeRate": 0.89991
}
```

## 🔄Autentificación
Para autenticarte, utiliza el siguiente token en el encabezado de la solicitud:

- **Método:** `POST`
- **Endpoint:** `/auth/login` (ajusta según tu implementación)

### 📥 Request Body
```json
{
  "username": "admin",
  "password": "123"
}
```

### 📥 Response
Esto retornara un token con el siguiente formato

```json
{
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Comandos Docker

docker build -t micro-money-exchange .

docker run -p 8080:8080 --name micro-money-exchange micro-money-exchange

```bash
docker tag micro-money-exchange yarovi/micro-money-exchange:v1.0

docker push yarovi/micro-money-exchange:v1.0

docker run -p 8080:8080 --name micro-money-exchange yarovi/micro-money-exchange:v1.0
```

La imagen esta disponible en :
https://hub.docker.com/repository/docker/yarovi/micro-money-exchange/general