import React from "react";
import { Link, useHistory } from "react-router-dom";

import style from "./NavBar.module.css"
const Navbar = (props) => {
 const history = useHistory()
  const onLogoutHandler = () => {
    props.onLoginHandler();
    history.push("/");
  }

  return (
    <nav className='navbar navbar-dark bg-success navbar-expand-md'>
      <div className='container'>
        <Link
          to='/'
          className='navbar-brand'
          style={{ textTransform: "uppercase", fontSize: "30px" }}
        >
          Student Registration
        </Link>
        <div className='collapse navbar-collapse'>
          <ul className='navbar-nav ml-auto'>
            <li className='nav-item'>
              <Link to='/studentList' className='nav-link'>
                Students
              </Link>
            </li>
            <li className='nav-item'>
              <Link to='/majors' className='nav-link'>
                Majors
              </Link>
            </li>
            <li className='nav-item'>
              <Link to='/about' className='nav-link'>
                About
              </Link>
            </li>
            <li className='nav-item'>
              <button className={style.button} onClick={onLogoutHandler}>
                Logout
              </button>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
