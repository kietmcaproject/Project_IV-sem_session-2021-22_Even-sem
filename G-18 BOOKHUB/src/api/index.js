import firebase from 'firebase';
// import {
//   FIREBASE_API_KEY,
//   FIREBASE_AUTH_DOMAIN,
//   FIREBASE_PROJECT_ID,
//   FIREBASE_STORAGE_BUCKET,
//   FIREBASE_MESSAGING_SENDER_ID,
//   FIREBASE_APP_ID,
// } from '@env'

// const firebaseConfig = {
//   apiKey: FIREBASE_API_KEY,
//   authDomain: FIREBASE_AUTH_DOMAIN,
//   projectId: FIREBASE_PROJECT_ID,
//   storageBucket: FIREBASE_STORAGE_BUCKET,
//   messagingSenderId: FIREBASE_MESSAGING_SENDER_ID,
//   appId: FIREBASE_APP_ID,
// };

const firebaseConfig = {
  apiKey: "AIzaSyCdwpZrDrFblMOvZq8AsPs7wd-DmgTeJlA",
  authDomain: "bookhub-760b5.firebaseapp.com",
  projectId: "bookhub-760b5",
  storageBucket: "bookhub-760b5.appspot.com",
  messagingSenderId: "31160505777",
  appId: "1:31160505777:web:0d7fca6c4284baf24a9990",
  measurementId: "G-Y40HZ7WF4P"
};

firebase.initializeApp(firebaseConfig);
