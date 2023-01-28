<#assign subject>Pre-Qualification decision for ${tenderId}</#assign>

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
                        <p>Dear User</p>
                        <#if applicationStatus == 'QUALIFIED'>
                        <p>We are glad to inform you that your organization ${contractorCompanyName} is ${applicationStatus} for project ${projectName}</p>
                        <p>As the next step for the RECOMMENDATION we request you to view and fill your response for Technical & Financial Bid Information</p>
                        <p>You can start filling additional information by clicking this
                         <a href="${appUrl}/tenders/edit-tender/${tenderId}">
                         link
                         </a>
                        <#else>
                         <p>We regret to inform you that your organization ${contractorCompanyName} is ${applicationStatus} for project ${projectName}</p>
                         <p>Your application to tender ${tenderId} will no longer be considered for future decisions of this project</p>
                         <p>You organization can explore other available contracts from our website. We wish you all the best for your future bids </p>
                        </#if>
                        <p>
                     </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>