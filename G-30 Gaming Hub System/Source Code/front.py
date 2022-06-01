import sys
import os
from tkinter import *
#import main

window=Tk()

window.title("Running Python Script")
window.geometry('289x511')
bg = PhotoImage(file = "background_image.png")
label = Label(window,image = bg)
label.place(x=0,y=0)
photo1 = PhotoImage(file = "bird.png")
photo2 = PhotoImage(file = "orange.png")
photo3 = PhotoImage(file = "white_lives.png")
def run1():
    os.system('flappy_bird.py')
def run2():
    os.system('fruit_ninja.py')
def run3():
    os.system('TTT.py')

btn = Button(window, text="FLAPPY BIRD",command=run1,image = photo1,compound=TOP)
                                                                                      #btn.grid(column=5, row=1)
btn.place(x=100,y=150)
btn = Button(window, text="FRUIT NINJA",command=run2,image = photo2,compound=TOP)
                                                                                      #btn.grid(column=5, row=4)
btn.place(x=100,y=250)
btn = Button(window, text="TIC TAC TOE",command=run3,image = photo3,compound=TOP)
                                                                                       #btn.grid(column=5, row=7)
btn.place(x=100,y=370)

window.mainloop()
