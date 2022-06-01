import { Table, TableWrapper, Cell, Row, Rows, Col, Cols } from 'react-native-table-component';
import { StatusBar } from 'expo-status-bar';
import React, { Component } from 'react';
import { Alert, TextInput, View,FlatList, StyleSheet,Text ,Image,ScrollView, Dimensions,TouchableOpacity,ActivityIndicator} from 'react-native';
import axios from 'axios';
import {  Input, Button } from 'native-base'
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Entypo,AntDesign  } from '@expo/vector-icons';
export default function Cart(props)
{
  const [ cart, setcart ] = React.useState({})
  const [ payment, setpayment ] = React.useState({})
  const [ total, settotal ] = React.useState(0)


	 const [ state, setstate ] = React.useState({tableHead:['Image','Title','Price(₹)','Quantity'],tableData: [
      ] });

   React.useEffect(()=>{
    if(props.route.params && props.route.params.cart)
      { 
    let bookid = Object.keys(props.route.params.cart)
    console.log("book id is ",props)
    setcart(props.route.params.cart)
    // https://madbookhub.herokuapp.com/
    axios.post('https://madbookhub.herokuapp.com/api/find/book/',{bookid }).then(resp=>{
      if(resp.data.success == 1)
      {
        console.log(resp.data.books)

        setstate({...state,tableData:resp.data.books})

        let books = resp.data.books
        let count=0;
        for(let x=0;x<books.length;x++)
        {
          count+= + books[x].price
        }
        settotal(count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)
        console.log("total count is =================",count)

      }
      console.log(resp.data)
    }).catch(err=>{
      console.log("Error is ",err)
    })

      }

   },[])

	      const element = (data, index) => (
      <TouchableOpacity onPress={() => download(data)}>
        <View style={styles.btn}>
          <Text style={styles.btnText}><Entypo name="download" size={20} color="#004080" /></Text>
        </View>
      </TouchableOpacity>
    );

        const handleIncDec=(bookid,type,price)=>{
          console.log("bookid is ",price)
          let temp=cart
          if(type=="inc")
          {
          temp[bookid]=cart[bookid]+1
          settotal(total+ +price)
          }
          else
          {
            if(cart[bookid]-1 >=0 )
            {              
              temp[bookid]=cart[bookid]-1
              settotal(total- +price)
            }
          }
          setcart({...temp})
          console.log("cart is ",cart)

        }

        const handlechange=(val,key)=>{
    let temp=payment
    if(key == "name" || key == "address")
    temp[key]= val.toLowerCase()
    else
    temp[key]= val
    
    setpayment({...temp})
    console.log(payment)

        }
        const checkOut=async (type)=>{
          console.log("payment are ",cart)
          function savePayment()
          {
   axios.post('https://madbookhub.herokuapp.com/api/save/payment',{...payment, method:type, cart:JSON.stringify(cart) }).then(resp=>{
      if(resp.data.success == 1)
      {
       console.log("Order booked successfully")
     

      }
    }).catch(err=>{
      console.log("Error is ",err)
    })

          }
    if(type=="cod")
          {

           savePayment()
           Alert.alert("Order booked successfully")
             props.navigation.navigate("Home")
          }
          else
          {
        console.log("redirecting to payment page ...")
        savePayment()
        props.navigation.navigate('Payment') 
          }

             try {
                // await AsyncStorage.removeItem("mycart")
                let mycart = await AsyncStorage.getItem("mycart")
              }
                  catch(err)
            {
              console.log(err)
            }
        }

	return(
    <>
    
		<View style={{backgroundColor:"#fff"}}>
     <ScrollView>
     <ScrollView horizontal={true} style={{width:Dimensions.get("window").width }} >
		 <Table borderStyle={{borderWidth: 2, borderColor: '#524A4E'}} style={{backgroundColor:"white",minHeight:330}}>
          <Row data={state.tableHead} style={styles.head} textStyle={styles.text}/>
          {

              state.tableData.map((cellData, cellIndex) => (
              <TableWrapper key={Math.random()} style={styles.row}>
                {
                 
                  <React.Fragment key={Math.random()} >
                    <Cell key={Math.random()} data={(<Image
        style={{width:50,height:50}}
        source={{
          uri: cellData.image,
        }}
      />)} textStyle={{margin: 7}} />
                    <Cell key={Math.random()} data={[cellData.title]} textStyle={{margin: 7,fontWeight:"bold"}}/>
                    <Cell key={Math.random()} data={[cellData.price]} textStyle={{margin: 7,fontWeight:"bold"}}/>
                    <Cell key={Math.random()} data={(<View style={{flexDirection:'row',alignItems:"center",overflow: "visible"}}><AntDesign name="minuscircleo" size={30} color="#1C658C" onPress={()=>{handleIncDec(cellData._id,"dec",cellData.price)}} /><Text style={{fontSize:20}}>  {cart[cellData._id]}   </Text><AntDesign name="pluscircleo" size={30} color="#1C658C" onPress={()=>{handleIncDec(cellData._id,"inc",cellData.price)}} /></View>)} textStyle={{margin: 0,fontWeight:"bold"}}/> 
                  
                  </React.Fragment>
                
                }
              </TableWrapper>
            ))
          }

    
        </Table>
          </ScrollView>
          </ScrollView >
        <ScrollView> 

          <Text style={{textAlign:"center",fontSize:20,color:'#1c658c'}}>Delivery Details</Text>
        <TextInput
          value={payment.name?payment.name:''}
          onChangeText={(name) => handlechange(name,"name")}
          placeholder={'Name *'}
          style={styles.input}
        />
            <TextInput
          value={payment.address?payment.address:''}
          onChangeText={(address) => handlechange(address,"address")}
          placeholder={' Address *'}
          style={styles.input}
        />
        <TextInput
          value={payment.pincode?payment.pincode:''}
          onChangeText={(pincode) => handlechange(pincode,"pincode")}
          placeholder={' Pincode *'}
            keyboardType='numeric'
           maxLength={6}
          style={styles.input}
        />

             <TextInput
          value={payment.phone?payment.phone:''}
          onChangeText={(phone) => handlechange(phone,"phone")}
          placeholder={' Phone *'}
            keyboardType='numeric'
           maxLength={10}
          style={styles.input}
        />

          <Button bg="#1c658c" mt="10" mx="10" isDisabled={total==0? true :false} onPress={()=>checkOut("pay")}>{total ? `Pay Now ₹ ${total}` : "Pay Now"}</Button>
  

  <Button bg="#1c658c" mt="5" mx="10" mb="30" isDisabled={total==0? true :false} onPress={()=>checkOut("cod")}>{total ? `Cash on delivery ₹ ${total}` : "Cash on delivery"}</Button>



   </ScrollView>
		</View>
      
        </>
		)
}

const styles = StyleSheet.create({

 container: { padding: 5, paddingTop: 30, backgroundColor: '#fff',minHeight:Dimensions.get("window").height },
  // input: {
  //   width: 200,
  //   height: 44,
  //   padding: 10,
  //   borderWidth: 1,
  //   marginTop:10,
  //   borderColor: 'black',
  // },

    input: {
    minWidth: 240,
    height: 44,
    padding: 10,
    borderWidth: 1,
    marginTop:10,
    borderColor: '#D8D2CB',
    borderRadius:4,
    marginLeft:20,
    marginRight:20
  },
    head: { height: 40, backgroundColor: '#D8D2CB',borderColor:"#524A4E",borderWidth:1,borderTopWidth:0 ,minWidth:Dimensions.get("window").width-5},
  text: { margin: 5 },
    row: { flexDirection: 'row', backgroundColor: '#EEEEEE',marginTop:5,padding:5},
    btn: { height: 20,  borderRadius: 2,maxWidth:80 },
  btnText: { textAlign: 'center', color: '#004080' }
});