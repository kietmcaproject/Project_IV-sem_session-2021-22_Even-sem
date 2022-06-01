import React from 'react'
import PropTypes from 'prop-types'
import { VStack, Center, Button } from 'native-base'
import { Alert, TextInput, View, StyleSheet,Text,KeyboardAvoidingView ,
  Image,ScrollView, Dimensions,TouchableOpacity,ActivityIndicator} from 'react-native';
  import Checkbox from 'expo-checkbox';
import * as DocumentPicker from 'expo-document-picker';
import {Picker} from '@react-native-picker/picker';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as FileSystem from 'expo-file-system';
export default AddBook = ({ navigation }) => {
  const [state,setstate] = React.useState({'for_sale':true})
   const [ btnloader, setbtnloader ] = React.useState(false);
   const [image, setImage] = React.useState(null);


  const handlePressOnSignIn = () => {
    navigation.navigate('SignIn')
  }

  const handlePressOnSignUp = () => {
    navigation.navigate('SignUp')
  }
  const handlechange=(val,key)=>{
    let temp=state
    if(key == "name" || key == "email" || key=="category")
    temp[key]= val.toLowerCase()
    else
    temp[key]= val
    
    setstate({...temp})
    console.log(state,"state is ======")

  }


const encode = (uri) => {
  if (Platform.OS === 'android') return encodeURI(`${uri}`)
  else return uri
}
  const handleUpload=  async (e)=>{

    console.log("inside handl upload============")
   
      console.log("before doc picker")
  
     let result = await DocumentPicker.getDocumentAsync({ type:'*/*'})
     // let uri = encode(result.uri)
     // const uri = FileSystem.documentDirectory+result.name;
     let uri = result.uri
console.log("uri is ",uri)



 //     .then(async (result)=>{
 //   console.log(result)
 //    console.log("+++++++++++")
 //    console.log("+++++++++++")
 //    console.log("+++++++++++")
 //    console.log("+++++++++++")

 const fileInfo = await FileSystem.getInfoAsync(uri);
 console.log("fileinfo is ",fileInfo)
 // setImage(result.uri)
    let fileBase64 = await FileSystem.readAsStringAsync(uri, { encoding: 'base64'  });
    // console.log(fileBase64)
    result.uri=`data:image/${result.uri.split(".").pop().toLowerCase()};base64,`+fileBase64
    // console.log("+++++++++++base 64 is ",result)
      // setImage(result.uri)
    setstate({...state,resume:result})
 //     }).catch(err=>{
 //       console.log(err)
 //     })
 
    // console.log("result i s",result)
   

  }





   const onSubmit=()=> {
    console.log("state is",state)
    //      && state.title && state.price && state.category && state.description
   if(Object.keys(state).length >0 && state.title && state.price && state.category && state.description && state.resume && state.resume.uri)
    {
     setbtnloader(true)


           var formData = new FormData();
        var list="123456789abcdefghijklmnopqrstuvwxyz";
   var name='';
   for(let i=0;i<15;i++)
   {
     name+=list.charAt(Math.floor(Math.random() * list.length))

   }
    //JSON.stringify({"uri":state.resume.uri,"type":`application/pdf`,"name":"mypdf.pdf"})
    // if(!state.resume.name.split(".").pop().endsWith("jpg"))
    formData.append("file",state.resume.uri)
  // else
  //     formData.append("file",state.resume)
     formData.append("upload_preset","bookhub")
    formData.append("cloud_name","dfk9dkjuf")
    // console.log("state resume file=================", state.resume)


    formData.append("public_id",`${name}.${state.resume.name.split(".").pop()}`)

        fetch("https://api.cloudinary.com/v1_1/dfk9dkjuf/auto/upload", {
      method: "post",
      body: formData
    }).then(res => res.json()).
      then(data => {
        // auth_user=props.route.params.auth_user
        // console.log("data uploadewd",JSON.stringify(data))
        console.log("+++++++++++++++++++++++++++++++++++++++++++++")
        console.log("+++++++++++++++++++++++++++++++++++++++++++++")

        console.log("===================data uploadewd=====================",JSON.stringify(state))

        // console.log("data uploadewd",JSON.stringify(auth_user))

          axios.post('https://madbookhub.herokuapp.com/api/save/book',{...state,secure_url:data.secure_url,keywords:state.category.split(",")}).then(resp=>{
    console.log(resp.data)
    if(resp.data.success == 1)
    {
     setstate({'for_sale':true})
    console.log("+++++++++++++++++++++++++++++++++++++inside then if",resp.data.message)
     Alert.alert("Details saved successfully")

    }
    else
    {
        console.log("+++++++++++++++++++++++++++++++++++++inside then else",resp.data.message)
     Alert.alert("Erro occured during detail saving") 
    }
     setbtnloader(false)
  }).catch(err=>{
    console.log(err)
     setbtnloader(false)
     Alert.alert("Some Technical Error occurred please try again")

  })
        // setPhoto(data.secure_url)
      }).catch(err => {
         setbtnloader(false)
        console.log("eror is ",err)
        Alert.alert("An Error Occured While Uploading")
      })
     
  

}

    else
    {
      Alert.alert("Please fill all the mandatory fields..")
    }

    // Alert.alert('Credentials', `${username} + ${password}`);
  }

  return (
    <>
    <View style={{paddingLeft:20,paddingRight:20}}>
          <Text style={{textAlign:"center",fontSize:20,color:"#1C658C"}}>Fill Book Details</Text>

            <Text style={{textAlign:"left",fontSize:15,fontWeight:"bold",marginTop:15,color:"#1C658C"}}>Book Title</Text>
        <TextInput
          value={state.title?state.title:''}
          onChangeText={(username) => handlechange(username,"title")}
          placeholder={'Title *'}
          style={styles.input}
        />
            <Text style={{textAlign:"left",fontSize:15,fontWeight:"bold",marginTop:5,color:"#1C658C"}}>Book Price</Text>

            <TextInput
          value={state.price?state.price:''}
          onChangeText={(salary) => handlechange(salary,"price")}
          placeholder={' Price *'}
          keyboardType='numeric'
          style={styles.input}
        />
            <Text style={{textAlign:"left",fontSize:15,fontWeight:"bold",marginTop:5,color:"#1C658C"}}>Book Description</Text>

        <TextInput
          value={state.description?state.description:''}
          multiline={true}
          onChangeText={(email) => handlechange(email,"description")}
          placeholder={' Description *'}
          style={{...styles.input,minHeight:44,height:'auto'}}
        />

   
            <Text style={{textAlign:"left",fontSize:15,fontWeight:"bold",marginTop:5,color:"#1C658C"}}>Book Category</Text>

{/*            <TextInput
          value={state.category?state.category:''}
          onChangeText={(phone) => handlechange(phone,"category")}
          placeholder={'Book Category (separated by comma) *'}
          style={styles.input}
        />*/}
        <View
  style={{
  borderWidth: 2,
  borderRadius: 4,
  borderColor:'#D8D2CB',
}}>
                 <Picker style={styles.pickerStyle}  
                        selectedValue={state.category?state.category:''}  
                        onValueChange={(itemValue, itemPosition) =>  
                            handlechange(itemValue, "category")}  
                    >  
                    <Picker.Item label="Action" value="action" />  
                    <Picker.Item label="Romance" value="romance" />  
                    <Picker.Item label="Adventure" value="adventure" />  
                </Picker> 
                </View> 

            <Text style={{textAlign:"left",fontSize:15,fontWeight:"bold",marginTop:5,color:"#1C658C"}}>For Sale</Text>

         <Checkbox
          value={state['for_sale']}
          onValueChange={(val)=>handlechange(val,'for_sale')}
          style={{transform: [{ scaleX: 1.3 }, { scaleY: 1.3 }]}}
           tintColors={{ true: '#1C658C', false: 'black' }}
        />

        <View>
<Text style={{marginTop:5}}> {state.resume && state.resume.uri ? <Text style={{color:"green"}}> (File Uploaded)</Text>:null}</Text>
         <Button bg="#1c658c" style={{marginTop:5}} onPress={handleUpload}>
    Book Image* 
      </Button>
        </View>


              <Button bg="#1c658c" style={{marginTop:25}} onPress={onSubmit} isLoading={btnloader}>
        Upload
      </Button>
        </View>
        </>

  )
}


const styles = StyleSheet.create({

    container: {
    // flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor:"red",
    backgroundColor: '#fff',
    minHeight:Dimensions.get('window').height-135
  },
  input: {
    minWidth: 240,
    height: 44,
    padding: 10,
    borderWidth: 2,
    marginBottom:10,
    borderColor: '#D8D2CB',
    borderRadius:4
  },
      pickerStyle:{  
        width: "100%",  
        color: '#1C658C',  
 
    }  

});