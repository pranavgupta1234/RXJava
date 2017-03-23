# RX Java

Reactive Extensions In Java deployed in android

## Getting Started

RX Java is the example of how functional reactive programming can ease out the work and implement the concept of observables and observeres.This uses rxjava 1.x and rxandroid 1.x dependencies 

## About This Repo

In this repo I had showed that how reactive extensions can be used for a simple form validation task.A naive way of implementation is to
just check for all the fields when user clicks on some button (seems boring) so lets try something else ?
Yes RX can do this work for you.I had shown that how we can observe for certain events and do some task from what they emit.That means 
there are some observers listening for some events (emitted by observables) and then observer reacts to those events (Thats why its "Reactive")
For this exampler application I had kept the rule as when Ist edittext contains "Pranav" and second edittext contains "pg" and third
contains a 10 digit number only then the button is enabled(ALL three conditions should be satisfied simultaneosly, I had used combineLatest in this case)


![](https://www.dropbox.com/s/ghjzei7mg5a1d8k/ezgif.com-video-to-gif.gif?raw=1)

## Authors

* **Pranav Gupta**

## License

This project is licensed under the MIT License

## Acknowledgments

* Inspiration from Jake Wharton and Kaushik Gopal

