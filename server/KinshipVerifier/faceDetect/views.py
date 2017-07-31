from django.shortcuts import render
from django.http import HttpResponse
from rest_framework import viewsets
from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import PictureData
from .serializers import PictureDataSerializer
from .serializers import UploadedBase64ImageSerializer
import datetime
import subprocess, os

@api_view(['GET', 'POST'])
def Result(request):
	os.chdir(r'C:\Users\CarlosGri\Development\projects\MVAPIFaceDetection\NRML\NRML')
	p1 = subprocess.call('matlab -nodisplay -nosplash -nodesktop -wait -r demo_nrml()')
	# (output, err) = p1.communicate()  

	# #This makes the wait possible
	# p_status = p1.wait()

	# #This will give you the output of the command being executed
	# print("Command output: " + output)
	percent = ""
	match = ""
	if p1 != 0:
		return Response("This did not work.", status=status.HTTP_400_BAD_REQUEST)
	else:
		with open(r'C:\Users\CarlosGri\Documents\result.txt') as fp:
			for i, line in enumerate(fp):
				if i == 0:
		            # 26th line
					percent = line
				elif i == 1:
		            # 30th line
					match = line
		return Response({'similarity': percent, 'match': match}, status=status.HTTP_201_CREATED)
	print(exitCode)
	return HttpResponse("Hello, world. You're at the polls index.")

class PictureDataViewSet(viewsets.ModelViewSet):
	queryset=PictureData.objects.all()
	serializer_class=PictureDataSerializer
	#serializer_class=UploadedBase64ImageSerializer
	# now = datetime.datetime.now()
	# file = 'R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=='
	# serializer = UploadedBase64ImageSerializer(data={'created': now, 'file': file})