import firebase from 'firebase/app'
import 'firebase/auth'

const firebaseConfig = {
  apiKey: "AIzaSyDCeB4Q603uVGg9S2Yz3izmI8swJ_kRIDs",
  authDomain: "ecommerce-4e949.firebaseapp.com",
  projectId: "ecommerce-4e949",
  storageBucket: "ecommerce-4e949.appspot.com",
  messagingSenderId: "868250284890",
  appId: "1:868250284890:web:acdb23ca9f4f913779b963"
};

  const fire = firebase.initializeApp(firebaseConfig);
  
  export default fire;