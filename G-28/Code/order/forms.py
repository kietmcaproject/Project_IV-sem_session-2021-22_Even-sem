from django import forms
from .models import Order

class OrderCreateForm(forms.ModelForm):
	DIVISION_CHOICES = (
		('Ghaziabad City', 'Ghaziabad City'),
		('Varanasi City', 'Varanasi City'),
		('Mirzapur City', 'Mirzapur City'),
		('Noida', 'Noida'),
		('Mumbai', 'Mumbai'),
	)

	DISCRICT_CHOICES = (
		('Ghaziabad.1', 'Ghaziabad.1'), 
		('Varanasi.2', 'Varanasi.2'),
		('Mirzapur.3', 'Mirzapur.3'),
		('Noida.4', 'Noida.4'),
		('Mumbai.5', 'Mumbai.5'),
	)

	PAYMENT_METHOD_CHOICES = (
		('PayTm', 'PayTm'),
		('Credit Card','Credit Card')
	)

	division = forms.ChoiceField(choices=DIVISION_CHOICES)
	district =  forms.ChoiceField(choices=DISCRICT_CHOICES)
	payment_method = forms.ChoiceField(choices=PAYMENT_METHOD_CHOICES, widget=forms.RadioSelect())

	class Meta:
		model = Order
		fields = ['name', 'email', 'phone', 'address', 'division', 'district', 'zip_code', 'payment_method', 'account_no', 'transaction_id']
