from celery import Celery
from celery.schedules import crontab

app = Celery('celery_app', broker='redis://localhost:6379/0')
app.conf.update(
    result_backend='redis://localhost:6379/0',
    beat_schedule={
        'add-every-30-seconds': {
            'task': 'tasks.add',
            'schedule': 10.0, # run every 10 seconds
            'args': (10, 10)
        },
        'multiply-at-noon': {
            'task': 'tasks.multiply',
            'schedule': crontab(hour='12', minute='6'),
            'args': (4, 5)
        }
    },
    include=['tasks']
)

# run this with 
# celery -A celery_app worker --loglevel=info

# run celery beat need 2 steps
# 1. celery -A celery_app worker --loglevel=info
# 2. celery -A celery_app beat --loglevel=info