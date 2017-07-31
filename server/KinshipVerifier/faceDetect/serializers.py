from rest_framework import serializers
from .models import PictureData
from drf_extra_fields.fields import Base64ImageField
import datetime

# class Base64ImageField(serializers.ImageField):
#     """
#     A Django REST framework field for handling image-uploads through raw post data.
#     It uses base64 for encoding and decoding the contents of the file.

#     Heavily based on
#     https://github.com/tomchristie/django-rest-framework/pull/1268

#     Updated for Django REST framework 3.
#     """

#     def to_internal_value(self, data):
#         from django.core.files.base import ContentFile
#         import base64
#         import six
#         import uuid

#         # Check if this is a base64 string
#         if isinstance(data, six.string_types):
#             # Check if the base64 string is in the "data:" format
#             if 'data:' in data and ';base64,' in data:
#                 # Break out the header from the base64 content
#                 header, data = data.split(';base64,')

#             # Try to decode the file. Return validation error if it fails.
#             try:
#                 decoded_file = base64.b64decode(data)
#             except TypeError:
#                 self.fail('invalid_image')

#             # Generate file name:
#             file_name = str(uuid.uuid4())[:12] # 12 characters are more than enough.
#             # Get the file name extension:
#             file_extension = self.get_file_extension(file_name, decoded_file)

#             complete_file_name = "%s.%s" % (file_name, file_extension, )

#             data = ContentFile(decoded_file, name=complete_file_name)

#         return super(Base64ImageField, self).to_internal_value(data)

#     def get_file_extension(self, file_name, decoded_file):
#         import imghdr

#         extension = imghdr.what(file_name, decoded_file)
#         extension = "jpg" if extension == "jpeg" else extension

#         return extension
class UploadedBase64Image(object):
    def __init__(self, file):
        self.file = file
        #self.created = created or datetime.datetime.now()

class UploadedBase64ImageSerializer(serializers.Serializer):
	file = Base64ImageField(required=False)
	#created = serializers.DateTimeField()

	def update(self, instance, validated_data):
		instance.file = validated_data['file']
		return instance

	def create(self, validated_data):
		return UploadedBase64Image(**validated_data)

class PictureDataSerializer(serializers.ModelSerializer):
	file=Base64ImageField()
	file2=Base64ImageField()
	class Meta:
		model=PictureData
		fields= ('data','file','file2')
	def create(self, validated_data):
		file=validated_data.pop('file')
		file2=validated_data.pop('file2')
		data=validated_data.pop('data')
		return PictureData.objects.create(data=data,file=file, file2=file2)