from tasks import add 

if __name__ == "__main__":
    result = add.delay(4, 5)
    print('Task result: ', result.get())
