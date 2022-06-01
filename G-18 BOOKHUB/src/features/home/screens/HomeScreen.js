import React from 'react'
import PropTypes from 'prop-types'
import axios from 'axios';
import { VStack, Center, Heading, Button, Divider , Thumbnail, List, ListItem } from 'native-base'
import {
  Text, 
  View,
  SafeAreaView,Dimensions,ScrollView,Image,TouchableOpacity } from 'react-native';
import Checkbox from 'expo-checkbox';
import { useUserContext } from '../../../context/UserContext'
import { useSignOut } from '../../../hooks/use-sign-out'
import Carousel from 'react-native-snap-carousel';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AntDesign } from '@expo/vector-icons';
import { useIsFocused } from '@react-navigation/native';

// ...
import {
  Collapse,
  CollapseHeader,
  CollapseBody
} from 'accordion-collapse-react-native';
export const HomeScreen = ({ navigation }) => {
  const isFocused = useIsFocused();
  const { width: viewportWidth, height: viewportHeight } = Dimensions.get('window');
const SLIDE_WIDTH = Math.round(viewportWidth / 2.6);
const ITEM_HORIZONTAL_MARGIN = 15;
const ITEM_WIDTH = SLIDE_WIDTH + ITEM_HORIZONTAL_MARGIN * 2;
const SLIDER_WIDTH = viewportWidth;
  const { user } = useUserContext()
  const [signOut, { isLoading }] = useSignOut()
  const [cartcount,setcartcount] = React.useState(0)
  const [cart,setcart] = React.useState({})
  const [itembycategory,setitembycategory] = React.useState({})
  const [carouseload , setcarouseload] = React.useState(true)
  const [searchbar , setsearchbar] = React.useState(false)

  const [searchfilter , setsearchfilter] = React.useState([])


  const [state,setstate] = React.useState({
          activeIndex:0,
          carouselItems: [
          {
              title:"Item 1",
              text: "Text 1",
          },
          {
              title:"Item 2",
              text: "Text 2",
          },
          {
              title:"Item 3",
              text: "Text 3",
          },
          {
              title:"Item 4",
              text: "Text 4",
          },
          {
              title:"Item 5",
              text: "Text 5",
          },
        ]
      })

  let carousel = React.useRef();

  React.useEffect(()=>{
    populateCart()
    async function populateCart()
    {
      let cart={}
       try {
                // await AsyncStorage.removeItem("mycart")
                let mycart = await AsyncStorage.getItem("mycart")
                console.log("mycart",mycart)
                if(mycart)
                {
                  cart=JSON.parse(mycart)
                }
                setcart(cart)
                setcartcount(Object.keys(cart).length)
            }
            catch(err)
            {
              console.log(err)
            }
      }

    
    axios.get("https://madbookhub.herokuapp.com/api/fetch/book/").then(resp=>{
      if(resp.data.success == 1)
      {
        setstate({...state,carouselItems:resp.data.books})

        let tempst = itembycategory
        tempst['action']=resp.data.books.filter((item)=>(item.keywords.includes("action") || item.keywords.includes("fight")))
        tempst['romance']=resp.data.books.filter((item)=>(item.keywords.includes("romance") || item.keywords.includes("romantic") || item.keywords.includes("love")))

        tempst['adventure']=resp.data.books.filter((item)=>item.keywords.includes("adventure"))


        // setitembycategory({...itembycategory ,action:resp.data.books.filter((item)=>item.keywords.includes("action"))})
        // setitembycategory({...itembycategory ,romance:resp.data.books.filter((item)=>item.keywords.includes("romance"))})
        // setitembycategory({...itembycategory ,adventure:resp.data.books.filter((item)=>item.keywords.includes("adventure"))})
       setitembycategory({...tempst})
        console.log("===========carousel loading is ",carouseload)
        
        setcarouseload(false)

        console.log("carousel loading is ",carouseload)
        // console.log("++++++++++++",resp.data.books.filter((item)=>item.keywords.includes("romance")))
   
      }

    }).catch(err=>{
      console.log("error occurred",err)
    })

  },[carouseload,isFocused])

  const handlePressOnUpdatePassword = () => {
    navigation.navigate('Addbook')      //Addbook
  }

  const goToCart=()=>{
 navigation.navigate('AddToCart',{cart})
  }
  const addToCart=async (id)=>{
    console.log("id is ",id)
    let cart = {}

              try {
                // await AsyncStorage.removeItem("mycart")
                let mycart = await AsyncStorage.getItem("mycart")
                console.log("mycart",mycart)
                if(mycart)
                {
                  cart=JSON.parse(mycart)
                }

                cart[id]=1

                console.log("cart is ",cart)
                setcart(cart)

                setcartcount(Object.keys(cart).length)
           await AsyncStorage.setItem("mycart",JSON.stringify(cart))
  } catch (error) {
    // Error saving data
    console.log("Error occured during remoing data")
  }

  }
 const renderItem = ({item, index}) => {
        return (
            <View style={{
              // backgroundColor:'green',
              borderColor:"#398AB9",
              borderWidth:2,
              borderRadius: 5,
              // height: 250,
              padding: 10,
              marginLeft: 10,
              marginRight: 10,textAlign:"center",display:"flex",justifyContent:"center",alignItems:"center" }}>
            <Image source={{
          uri: item.image,
        }}
        resizeMode={'contain'}
        style={{width:'100%',height:120}}
      />
            <Text style={{fontSize: 23}} numberOfLines={1}>{item.title}</Text>
            <Text numberOfLines={2} style={{textAlign:'center'}}>{item.description}</Text>
            <Text style={{fontWeight:'bold',marginTop:10}}>Rs(₹): {item.price}</Text>
                <Button bg="#fff" onPress={()=>addToCart(item._id)} style={{marginTop:10,borderColor:"#1c658c",borderWidth:2,padding:0,backgroundColor:"#1c658c"}} px="4" py="1" >{cart && cart[item._id] ? "Added ✅" : "Add to cart" }</Button>

          </View>

        );
    }


  return (
    <>
   
    <ScrollView>
    <View style={{flexDirection:"row",justifyContent:"space-between",paddingLeft:10,paddingRight:10,marginTop:10,alignItems:"center"}}>
            <Button bg="#1c658c" onPress={handlePressOnUpdatePassword}>Add Books</Button>
            <Text style={{color:"#1c658c",fontWeight:"bold",fontSize:20}} onPress={goToCart}><AntDesign name="shoppingcart" size={28} color="#1c658c" />{cartcount}</Text>
        <Button bg="#1c658c" onPress={signOut} isLoading={isLoading}>
          Sign out
        </Button>
            </View>


  <View style={{paddingTop:10}}>
 <Collapse 
          isExpanded={searchbar} 
          onToggle={(isExpanded)=>setsearchbar(isExpanded)}>
          <CollapseHeader>
            <Text style={{backgroundColor:"#1c658c",fontSize:15,textAlign:"center",fontWeight:"bold",color:"#fff",textDecorationLine: "underline",paddingBottom:3}}>Search By Category</Text>
          </CollapseHeader>
          <CollapseBody>
          <View style={{display:"flex",justifyContent:"space-around",flexDirection:"row",paddingTop:20}}>
                <View style={{display:"flex",alignItems:"center",justifyContent:"center"}}>
                  <Text >Action</Text>
                 <Checkbox
                value={searchfilter.includes("action")}
                onValueChange={()=>{searchfilter.includes("action") ? setsearchfilter(searchfilter.filter(e=>e!="action")) : setsearchfilter([...searchfilter, "action"])}}
                tintColors={{ true: '#1c658c'}}

              />
              </View>
                        <View style={{display:"flex",alignItems:"center",justifyContent:"center"}}>
                  <Text>Romantic</Text>
                 <Checkbox
                value={searchfilter.includes("romance")}
                onValueChange={()=>{searchfilter.includes("romance") ? setsearchfilter(searchfilter.filter(e=>e!="romance")) : setsearchfilter([...searchfilter, "romance"])}}
                tintColors={{ true: '#1c658c'}}

              />
              </View>
                        <View style={{display:"flex",alignItems:"center",justifyContent:"center"}}>
                  <Text>Adventure</Text>
                 <Checkbox
                value={searchfilter.includes("adventure")}
                onValueChange={()=>{searchfilter.includes("adventure") ? setsearchfilter(searchfilter.filter(e=>e!="adventure")) : setsearchfilter([...searchfilter, "adventure"])}}
                tintColors={{ true: '#1c658c'}}

              />
              </View>

          </View>
          </CollapseBody>
        </Collapse>


          {searchfilter.length > 0 ?  <View style={{marginTop:5}}>
       {searchfilter.map(book=>(
        <>

         <Text style={{fontSize:14,fontWeight:"bold",textAlign:"center",paddingBottom:10}}>Search Category : {book}</Text>

       <Carousel
              ref={(c) => { carousel = c; }}
              data={itembycategory[book] }
              renderItem={renderItem}
              sliderWidth={SLIDER_WIDTH}
              itemWidth={ITEM_WIDTH+15}
              activeSlideAlignment={'start'}
              inactiveSlideScale={1}
              inactiveSlideOpacity={1}
              loop={true}

            />
            </>

        ))}
       <View
  style={{
    borderBottomColor: 'black',
    borderBottomWidth: 1,
    marginTop:10
  }}
></View>

 
    </View>:     (!carouseload  ? <>
            <Text style={{fontSize:20,paddingRight:10,fontWeight:"bold",color:"#1C658C",textAlign:"center",marginTop:10}}>----- Action -----</Text>

    <View style={{marginTop:5}}>

       <Carousel
              ref={(c) => { carousel = c; }}
              data={itembycategory['action'] }
              renderItem={renderItem}
              sliderWidth={SLIDER_WIDTH}
              itemWidth={ITEM_WIDTH+15}
              activeSlideAlignment={'start'}
              inactiveSlideScale={1}
              inactiveSlideOpacity={1}
              loop={true}

            />

 
    </View>
<Text style={{fontSize:20,paddingRight:10,fontWeight:"bold",color:"#1C658C",textAlign:"center",marginTop:10}}>----- Romantic ----- </Text>
        <View style={{marginTop:5}}>
       <Carousel
              ref={(c) => { carousel = c; }}
              data={itembycategory['romance'] }
              renderItem={renderItem}
              sliderWidth={SLIDER_WIDTH}
            itemWidth={ITEM_WIDTH+15}
            activeSlideAlignment={'start'}
            inactiveSlideScale={1}
            inactiveSlideOpacity={1}
            loop={true}
            />

 
    </View>
<Text style={{fontSize:20,paddingRight:10,fontWeight:"bold",color:"#1C658C",textAlign:"center",marginTop:10}}>----- Adventure ----- </Text>
        <View style={{marginTop:5}}>
       <Carousel
              ref={(c) => { carousel = c; }}
              data={itembycategory['adventure']}
              renderItem={renderItem}
        sliderWidth={SLIDER_WIDTH}
  itemWidth={ITEM_WIDTH+15}
  activeSlideAlignment={'start'}
  inactiveSlideScale={1}
  inactiveSlideOpacity={1}
  loop={true}
            />

 
    </View>
</>:<Text>"loading..."</Text>) } 
  </View>

    </ScrollView>

    </>

  )
}

HomeScreen.propTypes = {
  navigation: PropTypes.object.isRequired,
}
