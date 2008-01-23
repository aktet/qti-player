package
{
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import model.Assessment;
	import model.Item;
	
	import module.TextModule;
	
	import mx.containers.Box;
	import mx.containers.Canvas;
	import mx.containers.HBox;
	import mx.containers.VBox;
	import mx.controls.Button;
	import mx.controls.Text;
	
	public class PageView extends Canvas
	{
		// ------------------------------------------------------------------------
		public function PageView(a:Assessment, c:Controller){
			super();
			assessment = a;
			controller = c;
			
			styleName = "pageview";
			item = assessment.items[controller.index];
			item.load(assessment.url, createLayout);
		}
		
		// ------------------------------------------------------------------------
		private function createLayout() : void{
			var header:Box = new HBox();
			var body:Box = new VBox();
			var footer:Box = new HBox();
			var atitle:Text = new Text();
			var ititle:Text = new Text();
			var prev_button:Button = new Button();
			var next_button:Button = new Button();
			var finish_button:Button = new Button();
			var counter:Text = new Text();

			// Create header			
			atitle.text = assessment.title;
			header.addChild(atitle);
			header.height = 50;
			header.styleName = "pageheader";
			
			// Create body
			ititle.text = item.title;
			body.addChild(ititle);
			createPage(body);
			body.styleName = "pagebody";
			
			// Create footer			
			prev_button.label = "Previous";
			prev_button.addEventListener(MouseEvent.CLICK, prevPage);
			if( controller.index == 0)
				prev_button.enabled = false;
			footer.addChild(prev_button);
			counter.text = (controller.index+1) + "/" + assessment.items.length;
			footer.addChild(counter);
			finish_button.label = "Finish";
			finish_button.addEventListener(MouseEvent.CLICK, resultPage);
			footer.addChild(finish_button);
			next_button.label = "Next";
			next_button.addEventListener(MouseEvent.CLICK, nextPage);
			if( controller.index+1 == assessment.items.length)
				next_button.enabled = false;
			footer.addChild(next_button);
			footer.height = 50;
			footer.styleName = "pagefooter";
			
			// Add panels
			addChild(header);
			addChild(body);
			addChild(footer);
				
		}

		// ------------------------------------------------------------------------
		private function prevPage(e:Event): void{
			controller.switchToPage(controller.index-1);
		}
		
		// ------------------------------------------------------------------------
		private function nextPage(e:Event): void{
			controller.switchToPage(controller.index+1);
		}
		
		// ------------------------------------------------------------------------
		private function resultPage(e:Event): void{
			controller.switchToPage(Controller.RESULT_PAGE);
		}
		
		// ------------------------------------------------------------------------
		private function createPage(parent :Box): void
		{
			for each(var tm:TextModule in item.modules){
				var text_box:Text = new Text();
				
				text_box.text = tm.text;
				parent.addChild(text_box);

				text_box = new Text();
				text_box.text = "----------------------------------------------";
				parent.addChild(text_box);
			}
		}
		
		// ------------------------------------------------------------------------
		// Private memberbs
    private var assessment:Assessment;
    private var controller:Controller;
		private var item:Item;
	}
}