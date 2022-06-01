import React, { useContext, useRef, useState } from "react";
import style from "./Login.module.css";
import { Link, Redirect, useHistory } from "react-router-dom";
import {userName , userPassword} from './auth';

const Login = (props) => {
  const emailRef = useRef();
  const passwordRef = useRef();
  const history = useHistory();
  const [error, setError] = useState("");
  const onLoginHandler = (e) => {
    e.preventDefault();
    const email = emailRef.current.value;
    const password = passwordRef.current.value;

      if (userName === email && userPassword === password) {
          props.onLoginHandler();
          history.push("/studentList")
      } else {
        setError("You've entered an invalid access credentials");
      }
  };

  return (
    <div className={style.login}>
      <h1>Admin Login</h1>
      <form onSubmit={onLoginHandler} className={style.form}>
        <div className={style.control}>
          <input type="email" id="email" ref={emailRef} placeholder="Email" />
        </div>
        <div className={style.control}>
          <input
            type="password"
            id="password"
            ref={passwordRef}
            placeholder="Password"
          />
        </div>
        {error !== "" && <p className={style.error}>{error}</p>}
        <div className={style.actions}>
          <button className="btn">Login</button>
        </div>
        <p>Forget your password?</p>
      </form>
    </div>
  );
};

export default Login;