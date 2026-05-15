# Namma HomeStay

Experience Local Living.

This Android application, developed using Kotlin, XML, and Firebase, offers a comprehensive homestay
management system with separate interfaces for travelers and homestay hosts. The app streamlines the
process of booking local stays while providing hosts with tools to list and manage
their properties efficiently.

---
## Features

### Traveler Side

- **Authentication**: Secure login using Firebase authentication.
- **Homestay Browsing**: View a list of stays with detailed information:
    - Homestay name
    - Location on Google Maps
    - Price (including GST)
    - Address
    - Number of available rooms
    - Capacity per room
    - Contact details (phone and email)
- **Search Functionality**: Find homestays by city or region.
- **Booking System**:
    - Book rooms with integrated Razorpay payment gateway.
    - View booking history.
- **Real-time Updates**: Automatic reduction of available rooms after booking.
- **User Profile**: View and edit account details.
- **Logout**: Secure sign-out option.

### Host Side

- **Authentication**: Separate signup and signin options for homestay hosts.
- **Homestay Management**:
    - List new homestays with comprehensive details.
    - Edit and update existing stay information.
    - Manage room availability.

## Technology Stack

- **Language**: Kotlin
- **UI**: XML (Material Design)
- **Backend**: Firebase
- **Maps Integration**: Google Maps API
- **Payment Gateway**: Razorpay

## Installation

1. Clone the repository:

```bash
git https://github.com/itssinghankit/NammaHomeStay.git
```

2. Install necessary dependencies.
3. Add `Google Map API key` from _Google Maps SDK_ to `secrets.properties`

```bash
MAPS_API_KEY= <Your API key>
```

4. Configure Firebase credentials.
5. Run the app on an Android device or emulator.

## Contributing

We welcome contributions to improve Namma HomeStay. Fork the repository, create a branch for your
changes, and submit a pull request.

## Contact

Feel free to reach out with any questions or feedback at
[support@nammahomestay.com](mailto:support@nammahomestay.com)
