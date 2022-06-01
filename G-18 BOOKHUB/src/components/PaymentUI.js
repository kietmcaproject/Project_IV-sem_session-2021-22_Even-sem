import React, { useEffect, useState } from 'react';
import { Alert,Text } from 'react-native';
import { useStripe } from '@stripe/stripe-react-native';
import * as stripe from '@stripe/stripe-react-native';
import Button from './Button';
import PaymentScreen from './PaymentScreen';
import { API_URL } from './Config';

export default function PaymentsUICompleteScreen({ navigation }) {
  const { initPaymentSheet, presentPaymentSheet } = useStripe();
  const [paymentSheetEnabled, setPaymentSheetEnabled] = useState(false);
  const [loading, setLoadng] = useState(false);
  const [clientSecret, setClientSecret] = useState();

  const fetchPaymentSheetParams = async () => {
    const response = await fetch(`${API_URL}/payment-sheet`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    const { paymentIntent, ephemeralKey, customer } = await response.json();
    console.log(customer)
    console.log(paymentIntent)
//     const pi = await stripe.paymentIntents.create({
//   amount: 1099,
//   currency: 'usd',
//   payment_method_types: ['card'],
// });

//     console.log("pi is ==================",pi)


    setClientSecret(paymentIntent);
    return {
      paymentIntent,
      ephemeralKey,
      customer
    };
  };




  const openPaymentSheet = async () => {
    if (!clientSecret) {
      return;
    }
    setLoadng(true);

    const data = await presentPaymentSheet({
      clientSecret,
    });
    console.log("client secret is ",clientSecret,data)

    if (data.error) {
      console.log(`Error code: ${data.error.code}`, data.error.message)
      Alert.alert(`Error code: ${data.error.code}`, data.error.message);
    } else {
      // Alert.alert('Success', 'The payment was confirmed successfully');
      Alert.alert("payment confirmed ")
      // navigation.navigate("Home")

      // Alert.alert(JSON.stringify(data))
    }

    setPaymentSheetEnabled(false);
    setLoadng(false);
    console.log("set btn loading false",loading)
  };

  const initialisePaymentSheet = async () => {
    const {
      paymentIntent,
      ephemeralKey,
      customer,
    } = await fetchPaymentSheetParams();
    console.log("intent",paymentIntent,ephemeralKey,customer)

    const { error } = await initPaymentSheet({
      customerId: customer,
      customerEphemeralKeySecret: ephemeralKey,
      paymentIntentClientSecret: paymentIntent,
      customFlow: false,
      merchantDisplayName: 'BookHub Team MNS',
      style: 'alwaysDark',
    });
    if (!error) {
      console.log("no error occured")
      setPaymentSheetEnabled(true);
    }
    else{
      console.log("Error occured ",err)
    }
  };

  useEffect(() => {
    // In your app’s checkout, make a network request to the backend and initialize PaymentSheet.
    // To reduce loading time, make this request before the Checkout button is tapped, e.g. when the screen is loaded.
    initialisePaymentSheet();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
        <PaymentScreen>
      <Button
        variant="primary"
        loading={loading}
        disabled={!paymentSheetEnabled}
        title="Checkout"
        onPress={openPaymentSheet}
      />
    </PaymentScreen>
    </>
  );
}
