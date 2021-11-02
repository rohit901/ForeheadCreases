# Mobile based Human Identification using Forehead Creases: Application and Assessment under COVID-19 Masked Face Scenarios
![Our Pipeline](Pipeline.png)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

This repository contains the code implementation for our paper, which was accepted at **[WACV 2022](https://wacv2022.thecvf.com)** conference.

### *The contents of this repository will be updated soon, please check back later*

## Table of Contents
- [Abstract](#abstract)
- [Usage](#usage)
- [Attribution](#attribution)
- [Database](#database)
- [Reference](#reference)

## Abstract
In the COVID-19 situation, face masks have become an essential part of our daily life. As mask occludes most prominent facial characteristics, it brings new challenges to the existing facial recognition systems. This paper presents an idea to consider forehead creases (under surprise facial expression) as a new biometric modality to authenticate mask-wearing faces. The forehead biometrics utilizes the creases and textural skin patterns appearing due to voluntary contraction of the forehead region as features. The proposed framework is an efficient and generalizable deep learning framework for forehead recognition. Face-selfie images are collected using smartphone's frontal camera in an unconstrained environment with various indoor/outdoor realistic environments. Acquired forehead images are first subjected to a segmentation model that results in rectangular Region Of Interest (ROI's). A set of convolutional feature maps are subsequently obtained using a backbone network. The primary embeddings are enriched using a dual attention network (DANet) to induce discriminative feature learning. The attention-empowered embeddings are then optimized using Large Margin Cosine Loss (LMCL) followed by Focal Loss to update weights for inducting robust training and better feature discriminating capabilities. Our system is end-to-end and few-shot; thus, it is very efficient in memory requirements and recognition rate. Besides, we present a forehead image dataset ([BITS-IITMandi-ForeheadCreases Images Database](http://ktiwari.in/projects/foreheadcreases/)) that has been recorded in two sessions from **247 subjects** containing a total of **4,964** selfie-face mask images. To the best of our knowledge, this is the first to date mobile-based forehead dataset and is being made available along with the mobile application in the public domain. The proposed system has achieved high performance results in both closed-set, i.e., CRR of 99.08% and EER of 0.44% and open-set matching, i.e., CRR: 97.84%, EER: 12.40% which justifies the significance of using forehead as a biometric modality.

## Usage
- Clone this repository to your local machine. 
```shell
$ git clone https://github.com/rohit901/Forehead-Creases.git
```
- Install the following required dependencies/prerequisite software.
  1. Anaconda: https://docs.anaconda.com/anaconda/install/index.html
  2. Pytorch: https://pytorch.org
  3. Jupyter Lab/Jupyter Notebook: https://jupyter.org/install
  4. Android Studio (to install and run the data collection application): https://developer.android.com/studio
  5. Any other missing dependencies can be installed by running `pip install <missing package name>`
- Code for the data-collection Android application can be found in folder named `Data_Collection_App`. Open this folder in Android Studio to use the application. To use this application, you would also require a backend-server with your backend API hosted. Change the API URL in the code accordingly.
- Network code can be run directly in [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/github/rohit901/ForeheadCreases/blob/main/Main_Network_Code.ipynb) or by downloading `Main_Network_Code.ipynb` file and opening it locally using jupyter notebook.

## Database
You can access the **BITS-IITMandi-ForeheadCreases Images Database** by visiting this [website](http://ktiwari.in/projects/foreheadcreases/) and filling up the database request form. After filling the form you will receive an email acknolwedgement of your submitted responses. Allow upto one week to receive update back from us. Please cite this paper in your work if you use the database.

## Reference
Bharadwaj, R., Jaswal, G., Nigam, A., & Tiwari, K. (2022). Mobile based human identification using forehead creases: application and assessment under COVID-19 masked face scenarios. 2022 IEEE Winter Conference on Applications of Computer Vision (WACV), accepted for publication. 

## Attribution
Camera2API and CameraX library from Android has been used to develop the data-collection Android application. Various other open-source libraries used in this project have been credited appropriately. 


