## 배포 URL

http://ec2-3-84-165-7.compute-1.amazonaws.com:8080

## Swagger URL

http://ec2-3-84-165-7.compute-1.amazonaws.com:8080/swagger-ui/index.html

## 회원가입 API
- path: http://ec2-3-84-165-7.compute-1.amazonaws.com:8080/api/users/signup
- request body:
```
{
  "username": "JIN HO2",
  "password": "12341234",
  "nickname": "Mentos"
}
```
- response: 
```
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "authorities": [
    {
      "authorityName": "ROLE_USER"
    }
  ]		
}
```

## 로그인 API
- http://ec2-3-84-165-7.compute-1.amazonaws.com:8080/api/users/sign
- request body:
```
{
	"username": "JIN HO",
	"password": "12341234"
}
```

- response:
```
{
	"token": "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL",
}
```

# Nickname check API
- path: http://ec2-3-84-165-7.compute-1.amazonaws.com:8080/api/users/1/nickname
- request
  - header: Authorization = "Bearer token"
  - body: none
