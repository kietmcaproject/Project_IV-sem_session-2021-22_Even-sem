import React from 'react'
import PropTypes from 'prop-types'
import {Text, Image} from 'react-native'
import { VStack, FormControl, Input, Button } from 'native-base'
import { useFormik, getIn } from 'formik'
import * as Yup from 'yup'

const buildValidationSchema = (withPasswordConfirmation) =>
  Yup.object({
    email: Yup.string().email().required("Please enter email"),
    password: Yup.string().required().min(6,"Your password must be longer than 5 characters."),
      //     email: Yup.string()
      //   .email("Invalid email address")
      //   .required("Please enter email"),
      // password: Yup.string().required("Please enter password"),
    // Optionally require password confirmation
    ...(withPasswordConfirmation && {
      passwordConfirmation: Yup.string()
        .oneOf([Yup.ref('password'), null])
        .required(),
    }),
  })

export const EmailAndPasswordForm = ({
  buttonText = 'Create account',
  isLoading,
  onSubmit,
  withPasswordConfirmation = false,
}) => {
  const { handleChange, handleBlur, handleSubmit, values, touched, errors } =
    useFormik({

      initialValues: {
        email: '',
        password: '',
        ...(withPasswordConfirmation && { passwordConfirmation: '' }),
      },
      validationSchema: buildValidationSchema(withPasswordConfirmation),
      onSubmit,
 
         enableReinitialize:false
    })

  return (
    <VStack space={4} alignItems="center" w="90%">
     <Image source={require('../../assets/booklogo.png')} style={{width:130,height:130,marginBottom:10,marginLeft:20}} />

      <FormControl
        isRequired
        isInvalid={getIn(errors, 'email') && getIn(touched, 'email')}
      >
        <FormControl.Label >Email</FormControl.Label>
        <Input
          autoCapitalize="none"
          keyboardType="email-address"
          onBlur={handleBlur('email')}
          onChangeText={handleChange('email')}
          value={values.email}
           style={{borderWidth:2,borderColor:"#D8D2CB"}}
        />
        {errors && errors.email ? <Text style={{color:"red"}}>{errors.email}</Text>: null}
      </FormControl>

      <FormControl
        isRequired
        isInvalid={getIn(errors, 'password') && getIn(touched, 'password')}
      >
        <FormControl.Label>Password</FormControl.Label>
        <Input
          autoCapitalize="none"
          secureTextEntry
          autoCorrect={false}
          autoCompleteType="password"
          onBlur={handleBlur('password')}
          onChangeText={handleChange('password')}
          value={values.password}
          style={{borderWidth:2,borderColor:"#D8D2CB"}}
        />
         {errors && errors.password &&errors.password.toLowerCase() != 'password is a required field' ? <Text style={{color:"red"}}>{errors.password}</Text>: null}
      </FormControl>

      {withPasswordConfirmation && (
        <FormControl
          isRequired
          isInvalid={
            getIn(errors, 'passwordConfirmation') &&
            getIn(touched, 'passwordConfirmation')
          }
        >
          <FormControl.Label>Confirm password</FormControl.Label>
          <Input
            autoCapitalize="none"
            secureTextEntry
            autoCorrect={false}
            autoCompleteType="password"
            onBlur={handleBlur('passwordConfirmation')}
            onChangeText={handleChange('passwordConfirmation')}
            value={values.passwordConfirmation}
 style={{borderWidth:2,borderColor:"#D8D2CB"}}
          />
        </FormControl>
      )}

      <Button bg="#1c658c" onPress={handleSubmit} isLoading={isLoading}>
        {buttonText}
      </Button>
    </VStack>
  )
}

EmailAndPasswordForm.propTypes = {
  buttonText: PropTypes.string,
  isLoading: PropTypes.bool.isRequired,
  onSubmit: PropTypes.func.isRequired,
  withPasswordConfirmation: PropTypes.bool,
}
