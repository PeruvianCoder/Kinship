from django.db import models
from rest_framework import serializers
from drf_extra_fields.fields import Base64ImageField
import datetime

class PictureData(models.Model):
	# username=models.CharField(max_length=200)
	# password=models.CharField(max_length=200)
	# img1 = Base64ImageField(required=False)
	# img2 = Base64ImageField(required=False)
	# data1=models.BinaryField()
	# data2=models.BinaryField()
	file=models.ImageField(upload_to='geo_entity_pic', default='False.jpg')
	data=models.CharField(max_length=1000, default='0000000')