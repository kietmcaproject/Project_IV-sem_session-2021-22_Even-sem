import React from 'react'
import PropTypes from 'prop-types'
import { VStack, Center, Button } from 'native-base'
import { Image, View, Text } from 'react-native';
export const GuestWelcomeScreen = ({ navigation }) => {
  const handlePressOnSignIn = () => {
    navigation.navigate('SignIn')
  }

  const handlePressOnSignUp = () => {
    navigation.navigate('SignUp')
  }

  return (
    <Center flex={1}>
      <View style={{width:180,height:180,backgroundColor:"#D8D2CB",borderRadius:90,position:'absolute',top:-50,right:-50}}>
       <View style={{width:130,height:130,backgroundColor:"#398AB9",borderRadius:65,position:"absolute",top:0,right:0}}></View>
      </View>
         <View style={{width:180,height:180,backgroundColor:"#D8D2CB",borderRadius:90,position:'absolute',bottom:-50,left:-50}}>
       <View style={{width:130,height:130,backgroundColor:"#398AB9",borderRadius:65,position:"absolute",bottom:0,left:0}}></View>
                 
      </View>
      <VStack space={4} alignItems="center" w="90%">
      <Text style={{color:"#1C658C",fontSize:25,fontWeight:"bold",fontFamily:"serif"}}>Welcome to BookHub</Text>
     <Image source={require('../../../../assets/book.png')} style={{width:190,height:190,marginBottom:10,marginLeft:20}} />
        <Center>
          <Button bg="#1c658c" onPress={handlePressOnSignIn}>Sign in</Button>
        </Center>
        <Center>
          <Button bg="#1c658c" onPress={handlePressOnSignUp}>Sign up</Button>
        </Center>
      </VStack>
    </Center>
  )
}

GuestWelcomeScreen.propTypes = {
  navigation: PropTypes.object.isRequired,
}
