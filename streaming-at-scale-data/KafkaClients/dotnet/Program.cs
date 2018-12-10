using System;
using System.Configuration;

namespace EventHubsForKafkaSample
{
    class Program
    {
        public static void Main(string[] args)
        {
            string brokerList = ConfigurationManager.AppSettings["EH_FQDN"];
            string connectionString = ConfigurationManager.AppSettings["EH_CONNECTION_STRING"];
            string topic = ConfigurationManager.AppSettings["EH_NAME"];
            string caCertLocation = ConfigurationManager.AppSettings["CA_CERT_LOCATION"];
            string consumerGroup = ConfigurationManager.AppSettings["CONSUMER_GROUP"];
            string continueWriting = "y";

            
            while(continueWriting=="y")
            {
                Console.WriteLine("Initializing Producer");
                Worker.Producer(brokerList, connectionString, topic, caCertLocation).Wait();
                //Console.WriteLine("Enter 'y' if you want to continue sending messages");
                //continueWriting = Console.ReadLine();
                //Console.WriteLine("Initializing Consumer");
                //Worker.Consumer(brokerList, connectionString, consumerGroup, topic, caCertLocation);
                //Console.ReadKey();
            }

        }
    }
}
