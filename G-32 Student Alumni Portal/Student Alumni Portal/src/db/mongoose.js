const mongoose = require("mongoose");
dbConnect()
async function dbConnect(){

     try {
         await mongoose.connect('mongodb+srv://vaibhav:vaibhav@cluster0.nv24w.mongodb.net/AlumniPortalSystem' ,
                          {useCreateIndex:true ,useNewUrlParser:true,useUnifiedTopology: true });
         console.log('Mongo DB Connection success')
     } catch (error) {
         console.log('Mongo DB Connection failed')
     }
}

module.exports = mongoose