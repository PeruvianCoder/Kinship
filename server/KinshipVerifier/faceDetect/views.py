from django.shortcuts import render
from django.http import HttpResponse
from rest_framework import viewsets
from .models import PictureData
from .serializers import PictureDataSerializer
from .serializers import UploadedBase64ImageSerializer
import datetime

# def index(request):
#     return HttpResponse("Hello, world. You're at the polls index.")

class PictureDataViewSet(viewsets.ModelViewSet):
	queryset=PictureData.objects.all()
	serializer_class=PictureDataSerializer
	#serializer_class=UploadedBase64ImageSerializer
	# now = datetime.datetime.now()
	# file = 'R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=='
	# serializer = UploadedBase64ImageSerializer(data={'created': now, 'file': file})