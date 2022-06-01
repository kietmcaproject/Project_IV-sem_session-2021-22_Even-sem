import React from "react";
import { useHistory } from "react-router-dom";
import "./Checkout.css";

function CheckOut() {
  const history = useHistory();
  function checkoutFormHandler(event) {
    event.preventDefault();
    alert("You Order is Placed!!! Thanks for Placing the order");
    history.push("/");
  }

  return (
    <div>
      <h1>CheckOut </h1>
      <form onSubmit={checkoutFormHandler} className="checkout">
        <div className="formControl">
          <label>Adress</label>
          <textarea rows="5" cols="50" required />
        </div>
        <div className="formControl">
          <label>Pincode</label>
          <input type="number" required />
        </div>
        <div className="formControl">
          <label>City</label>
          <input type="text" required />
        </div>
        <div className="formControl">
          <label>State</label>
          <input type="text" required />
        </div>
        <div className="formControl">
          <label>Country</label>
          <input type="text" required />
        </div>
        <button className="formAction">Click for Payment</button>
      </form>
    </div>
  );
}

export default CheckOut;
