<#assign subject>Enquiry - Contact Us</#assign>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

        <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>

        <!-- use the font -->
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                font-size: 14px;
            }
        </style>
	</head>

	<body style="margin: 0; padding: 0;">
        <table align="center" border="0" cellpadding="0" cellspacing="0" width="900" style="border-collapse: collapse;">
            <tbody>
                <tr>
                    <td style="padding: 40px 0 0 0;">
                        <img src="cid:logo.png" alt="https://www.gummadibuilt.com" style="display: block;" />
                    </td>
                </tr>

                <tr>
                     <td bgcolor="#eaeaea" style="padding: 40px 30px 40px 30px;">
                        <p>Dear Admin(s)</p>
                        <p>User ${userName} is enquiring about role ${appRole}</p>
                        <p>User email-address: ${emailAddress}, mobile number: ${mobileNumber}</p>
                        <p>Enquiry Description: ${description}</p>
                     </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>