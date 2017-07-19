from django.conf.urls import include,url
from django.contrib import admin
from faceDetect import views
from rest_framework import routers

router=routers.DefaultRouter()
router.register(r'PictureData', views.PictureDataViewSet)

urlpatterns = [
   # url(r'^$', views.index, name='index'),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^', include(router.urls))
    #url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
