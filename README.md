# :bird: twitter4d

### :writing_hand: Description
twitter4d is a simple Discord bot written using Javacord and twitter4j. It allows server administrators to stream new tweets to specific text channels.

### :link: How do I add this to my server?
Although this bot is in very early stage and may not be 100% reliable you can click [HERE](https://discord.com/api/oauth2/authorize?client_id=810925444756668417&permissions=18496&scope=bot) to invite this discord bot to your server.

### :keyboard: Command usage
**Remember** to exclude the @ symbol from twitter names.

| Command | Arguments | Description | Required Permissions |
| ------- | --------- | ----------- | -------------------- |
| \>follow | twitter name | Follow an account. New tweets from this account will be sent to the channel where the command is sent. | MANAGE_CHANNELS |
| \>unfollow | twitter name | Unfollow an account. | MANAGE_CHANNELS |
| \>following |  | List the followed accounts on your server and the channels where new tweets will be sent. |
| \>invite |  | Shows the invite url. |

### :microphone: Support Discord server
If you are having any problems not listed below or would like to see a showcase of the bot in action click [HERE](https://discord.gg/nwTBrMYQtn) to join the official twitter4d support server.

### :computer: Contributing
If you would like to make any changes, feel free to make a pull request. If you encounter any issues and have no programming experience please open an issue, and I will take a look. 

### :question: Frequently asked questions
##### Why is the bot not sending new tweets?
Make sure the bot has all the correct permissions. It needs to be able to read messages in the channel to see the initial >follow command as well as be able to send messages when the twitter account you follow creates a new tweet.


##### How do I unfollow all twitter accounts quickly?
Despite the fact there is no command to do this, you can kick the bot from your server and then re-invite it. 

##### Are there any limits to the number of accounts I can follow?
At the moment there are no limits enforced by the bot limiting the number of twitter accounts you can follow. However, the twitter api limits the number of follow streams to 5000. This means that in the future I may have to enforce limits however if this comes to pass I will give plenty of notice to server owners.