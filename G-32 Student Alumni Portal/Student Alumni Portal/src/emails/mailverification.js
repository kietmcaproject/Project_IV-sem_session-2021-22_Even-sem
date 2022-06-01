const nodemailer=require('nodemailer')
const jwt=require('jsonwebtoken')

const mailverification=(emailid,id)=>{
    console.log('yes')
    const transporter=nodemailer.createTransport({
        service:'gmail',
        auth:{
            user:'vaibhav.2023mca1102@kiet.edu',
            pass:'princessanu@27'
        }
    })
    
    const token=jwt.sign({_id:id,type:'mailverification'},'thisismyjwtsecret')
     const url=`https://aluminitrackingsystem.herokuapp.com/user/mailverification?token=${token}`
    const mailOption={
        from:'vaibhav.2023mca1102@kiet.edu',
        to:emailid,
        subject:'Verify your gmail for kiet Mca Alumini user.',
        
        html:`<p>Thanks For Resistering With Us Click <a href=${url}>here</a> to verify your email</p>`
    }

    transporter.sendMail(mailOption,(err,data)=>{
        if(err){
            console.log('Mail not Sent for verify')
        }else{
            console.log('Mail sent for verify!')
        }
    })
}



const resetpassword=async(emailid)=>{
    const transporter=nodemailer.createTransport({
        service:'gmail',
        auth:{
            user:'vaibhav.2023mca1102@kiet.edu',
            pass:'princessanu@27'
        }
    })

    const token=jwt.sign({emailid,type:'resetpassword'},'thisismyjwtsecret2')
   const url=`https://aluminitrackingsystem.herokuapp.com/user/reset-password?token=${token}`
    const mailOption={
        from:'vaibhav.2023mca1102@kiet.edu',
        to:emailid,
        subject:'Reset your passowrd',
        html:`<p>Click <a href=${url}>here</a> to reset your password</p>`
    }


    transporter.sendMail(mailOption,(err,data)=>{
        if(err){
            console.log('Mail not sent for passowrd!!')
        }else{
            console.log('Mail sent for passowrd!!!')
        }
    })
}
//collage verification
const collageverification=(emailid,id,name,batch)=>{
    console.log('yes')
    const transporter=nodemailer.createTransport({
        service:'gmail',
        auth:{
            user:'vaibhav.2023mca1102@kiet.edu',
            pass:'princessanu@27'
        }
    })
    
    const token=jwt.sign({_id:id,type:'collageverification'},'thisismyjwtsecret')
     const url=`https://aluminitrackingsystem.herokuapp.com/user/collageverification?token=${token}`
    const mailOption={
        from:'vaibhav.2023mca1102@kiet.edu',
        to:emailid,
        subject:'Verify Alumini',
        
        html:`<p> student named ${name} batch ${batch } want to register <a href=${url}>here</a> to verify your alumini</p>`,
        
    }

    transporter.sendMail(mailOption,(err,data)=>{
        if(err){
            console.log('Mail not Sent for collageverification')
        }else{
            console.log('Mail sent for collageverification!')
        }
    })
}
module.exports={
    mailverification,
    resetpassword,
    collageverification
}