laundry-check
=============

A command line tool that checks for French Laundry reservations in the next two months.  It's already found me a few at rather inconvenient times.

Uses the Twilio API, so be sure to set the necessary API keys in the environment before running (see SmsService).

## Usage

```
java -jar laundryCheck.jar [-party <Party Size>] [-phone <Number to Text>]
```

## Author

Justin Appler: [@justinappler][twitter]

## License

Licensed under [MIT][mit].

[twitter]: http://twitter.com/justinappler
[mit]: http://www.opensource.org/licenses/mit-license.php
