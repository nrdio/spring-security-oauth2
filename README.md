# This repository demonstrates usage of Spring Security OAuth2.0, Spring Security and Spring Cloud Gateway

It also demonstrates best practices and patterns including gateway, circuit breaker, distributed tracing using sleuth and zipkin 
  
Repository contains following modules

   * [Authorization Server](authorization-server) 
   * [Resource Server](resource-server)
   * [API Gateway](api-gateway) 

## Technologies and Frameworks

  * Spring Boot
  * Spring Security OAuth2.0
  * Spring Security
  * Spring Cloud Gateway
  * MySQL
  * H2 (Integration Tests)
  * Sleuth and Zipkin
  * Java 11
  * Docker

## Prerequisite

  * Java 11+
  * Docker

## How to run?

Package Authorization Server, Resource Server and API Gateway and run integration tests using following command

```
mvn clean verify -PIT
```

Fire up these services using docker compose. It will also spin up MySQL8 and Zipkin. 
  
```
docker-compose up
```

## OAuth2 Overview and Test

OAuth standard was introduced primarily for enabling access to protected resources owned by resource owner and with their 
authorization to client applications. Please refer to [RFC6749](https://tools.ietf.org/html/rfc6749) for more details. 


### OAuth2 Roles 

OAuth2 defines four roles -

  * **Resource Owner**: An entity capable of granting access to a protected resource
  * **Resource Server**: The server hosting the protected resources, capable of accepting and responding to protected resource requests using access tokens.
  * **Client**: An application making protected resource requests on behalf of the resource owner and with its authorization.
  * **Authorization server**: The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization.

### Authorization Grant 

There are four grants. Client application uses one of the grant types to get access to protected resource using access token issued by authorization server based on resource owners authorization

### Authorization Code Grant

The client directs the resource owner to an authorization server with authorization request. Authorization server authenticate resource owner, shows client details and authorization scopes requested by client and 
obtains authorization. It redirects back to redirect uri of the authorization server with authorization code. 

Client exchange authorization code with authorization server for access and refresh token.

There on client can make request to Resource Server using access token as long as it's valid. 

Refresh token can be used to obtain new access and refresh token. 
 

#### Authorization Request

Client `clientx` constructs authorization uri to requests authorization from resource owner `userx`. 

```
http://localhost:8085/oauth/authorize?client_id=clientx&response_type=code&scope=payments&state=5ca75bd30&redirect_uri=https://localhost:8080/oauth
```
   
URI - http://localhost:8085/oauth/authorize 

Params -

* client_id - Clietx's client id received from authorization server
* response_type - code
* scope - custom payments scope
* state - Opaque value to maintain state between the request and the callback
* redirect_uri - Clientx's preregistered redirect URI  
 
 
Authorization server will verify `userx` identity first. Use `userx` as username and `password` as password.

![authorization code I](doc/authorization-code-I.png)


Post authentication `userx` will be prompted to authorize requested scope by `clientx`  


![authorization code II](doc/authorization-code-II.png)

#### Authorization Response

Authorization server will redirect to `redirect_uri` of `clientx` with `code` and `state` as querystring params 

```
https://localhost:8080/oauth?code=QXTSKs&state=5ca75bd30
```

#### Access Token Request

Client application exchanges authorization code for access and refresh token. If client type is confidential then client authentication is mandatory. In this case client id(`clientx`) and password(`password`) are included as Basic auth.   

```
curl -i -X POST \
  http://localhost:8888/oauth/token \
  -H 'Authorization: Basic Y2xpZW50eDpwYXNzd29yZA==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=authorization_code&code=QXTSKs&redirect_uri=https%3A%2F%2Flocalhost%3A8080%2Foauth'
``` 

#### Access Token Response

Authorization server will verify validity of authorization code and optionally authenticate client (optional for public clients). Post verification it will issue access and refresh token to client.  

```
HTTP/1.1 200 
Cache-Control: no-store
Pragma: no-cache
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Mon, 30 Dec 2019 14:24:46 GMT

{"access_token":"b6a0e021-802c-4b79-a835-4ac79ffd6599","token_type":"bearer","refresh_token":"b1dfaf04-d917-417a-9a95-8e12ad34c83a","expires_in":22960,"scope":"payments"}
```

Now access_token can be used to call protected resource server endpoint ex. userinfo

```
curl -i -X GET \
  http://localhost:8888/userinfo \
  -H 'Authorization: Bearer b6a0e021-802c-4b79-a835-4ac79ffd6599'
  
HTTP/1.1 200 OK
transfer-encoding: chunked
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Date: Mon, 30 Dec 2019 14:30:28 GMT

{"name":"userx"}
```

Client can use refresh_token to request new access_token in case access_token is not valid anymore

```
curl -i -X POST \
  http://localhost:8888/oauth/token \
  -H 'Authorization: Basic Y2xpZW50eDpwYXNzd29yZA==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=refresh_token&refresh_token=b1dfaf04-d917-417a-9a95-8e12ad34c83a'

{"access_token":"62b7edcd-c05c-4409-8ae9-1dd6b839c1b5","token_type":"bearer","refresh_token":"b1dfaf04-d917-417a-9a95-8e12ad34c83a","expires_in":28799,"scope":"payments"}

```

### Implicit Grant

Normally used by public clients running in browser typically as JavaScript applications. Since the client is public 
Authorization Server does not rely on client authentication. Instead it relies on redirect uri registered by client. Refresh token is not issued for implicit grant. 
Client constructs authorization request similar to authorization code flow except for response_type. Response type is token for implicit grant. 
This grant type should be used only when other grant types can't be used as access token is exposed on user agent.  

#### Authorization Request

```
http://localhost:8085/oauth/authorize?client_id=clientx&response_type=token&scope=payments&state=5ca75bd30&redirect_uri=https://localhost:8080/oauth

HTTP/2 200 
content-type: application/json
content-length: 0
date: Mon, 21 May 2018 19:43:20 GMT
x-amzn-requestid: 36d8e4cc-5d2f-11e8-bdd8-83a00846daaf
x-amz-apigw-id: HQIlvGWuvHcFZ0A=
x-amzn-trace-id: Root=1-5b032157-fa9d45eda83d906634f26893
x-cache: Miss from cloudfront
via: 1.1 ce2b03db99d40501c5695fce9dfbb777.cloudfront.net (CloudFront)
x-amz-cf-id: VGNK0CozL3ipCbGWxsrGZjmArmt_pF9FmOutyCPpwbEp8I0h5uYKIQ==
```

#### Access Token Response

Authorization Server directly issues access_token as a query string param of redirect_uri after obtaining authorization from resource owner 

```
https://localhost:8080/oauth#access_token=62b7edcd-c05c-4409-8ae9-1dd6b839c1b5&token_type=bearer&state=5ca75bd30&expires_in=27859
```

### Resource Owner Password Credentials Grant

Normally used when resource owner has a trust relationship with client application. Resource owner enters his credentials in clint application
and client applications exchange them for access token and optional refresh token with authorization server. This grant type should be used only
if other grant types can't be used and resource owner trusts the client application.
   

#### Access Token Request

Client can optionally include scope parameter

```
curl -X POST \
  http://localhost:8888/oauth/token \
  -H 'Authorization: Basic Y2xpZW50eDpwYXNzd29yZA==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&username=userx&password=password'
```

#### Access Token Response

Authorization Server will verify client credentials and resource owners credentials. Post verifying credentials it will issue access and refresh tokens. 

```
HTTP/1.1 200 OK
transfer-encoding: chunked
Cache-Control: no-store
Pragma: no-cache
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Date: Mon, 30 Dec 2019 20:24:35 GMT

{"access_token":"84fcc436-e966-4f75-8f60-27d1ac8a68fa","token_type":"bearer","refresh_token":"67552fad-3b9a-4d0c-b901-abf7717901d3","expires_in":28799,"scope":"payments accounts"}
```

### Client Credentials Grant

Client can request access token using client credentials to access his own protected resources using this grant.
   

#### Access Token Request

Client can optionally include scope parameter

```
curl -i -X POST \
  http://localhost:8888/oauth/token \
  -H 'Authorization: Basic Y2xpZW50eDpwYXNzd29yZA==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials'
```

#### Access Token Response

Authorization Server will verify client credentials and issue access token
```
HTTP/1.1 200 
Cache-Control: no-store
Pragma: no-cache
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 31 Dec 2019 12:13:30 GMT

{"access_token":"0493792f-8b8e-4f2f-a995-29ede896bd5d","token_type":"bearer","expires_in":28776,"scope":"payments accounts"}
```

### Revoke Token

Client application can revoke the access and refresh token using revoke_token request. This endpoint is not part of 
[RFC6749](https://tools.ietf.org/html/rfc6749). However in situations like when resource owner want to deregister 
from client application then client application may need to revoke the tokens received from Authorization Server so that 
he can't access protected resource owned by resource owner. 

#### Revoke request

token_type_hint parameter can be refresh_token or access_token

```
curl -i -X POST \
  http://localhost:8888/oauth/token/revoke \
  -H 'Authorization: Basic Y2xpZW50eDpwYXNzd29yZA==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'token_type_hint=refresh_token&token=a99d3603-ede5-441c-853f-0db8cf46daa4'
```

#### Revoke response 

Revoking refresh_token will also revoke access_token.

```
HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=C440167A8E5CB1BE37D54CB71B503A26; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Tue, 31 Dec 2019 12:30:31 GMT
```

## Distributed tracing

Spring Cloud Sleuth and zipkin is used by Authorization Server, Resource Server and API Gateway to generate publish Zipkin-compatible 
traces via HTTP. You can use Zipkin UI available at http://localhost:9411 for visualization. 

![Zipkin](doc/zipkin.png)

