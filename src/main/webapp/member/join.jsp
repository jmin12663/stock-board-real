<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>회원가입</title>
</head>
<body>

<h2>회원가입</h2>

<form action="/KoStock/member/join.do" method="post">
    이름: <input type="text" name="name"><br />
    아이디: <input type="text" name="userid"><br />
    비밀번호: <input type="password" name="password"><br />

    <button type="submit">가입하기</button>
</form>
</body>
</html>
