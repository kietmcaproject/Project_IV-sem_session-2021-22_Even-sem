import React, { useEffect, useState } from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";

import "bootstrap/dist/css/bootstrap.min.css";

import { useAppContext } from "./context/AppContext";

import Navbar from "./components/layout/Navbar";
import About from "./components/layout/About";
import StudentList from "./components/student/StudentList";
import StudentDetails from "./components/student/StudentDetails";
import AddStudent from "./components/student/AddStudent";
import EditStudent from "./components/student/EditStudent";
import MajorList from "./components/major/MajorList";
import AddMajor from "./components/major/AddMajor";
import Login from "./components/Authentication/Login";
import './App.css'

const App = () => {
  const { getStudents, getMajors } = useAppContext();

  const [loginStatus, setLoginStatus] = useState(false);

  const onLoginHandler = () => {
    setLoginStatus(!loginStatus)
  }

  useEffect(() => {
    getStudents();
    getMajors();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (

    <BrowserRouter>
      {loginStatus && <Navbar onLoginHandler={onLoginHandler}/>}
      {/* <div className=' background' /> */}
      <div className=' container'>
        <Switch>
          {!loginStatus && <Route exact path="/"><Login onLoginHandler={onLoginHandler}/></Route>}
          <Route exact path='/studentList'> <StudentList /> </Route>
          <Route exact path='/about' component={About} />
          <Route exact path='/students/add' component={AddStudent} />
          <Route exact path='/students/edit/:id' component={EditStudent} />
          <Route exact path='/students/:id' component={StudentDetails} />
          <Route exact path='/majors' component={MajorList} />
          <Route exact path='/majors/add' component={AddMajor} />
        </Switch>
      </div>
    </BrowserRouter>
  );
};

export default App;
