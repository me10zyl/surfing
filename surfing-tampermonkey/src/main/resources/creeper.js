(function(root){
	class Creeper{
		// @grant        GM_xmlhttpRequest
		// @grant        GM_log
		ajax(opts){
			let self = this;
			return new Promise(function(accept, reject){
				let option = self.extend({
					onload : function (data) {
						accept(data);
					}, onerror : function(data){
						GM_log('error', data);
						reject(data);
					}
				}, opts)
				GM_xmlhttpRequest(option);
			});
		}
		ajaxGet(url, data){
			return this.ajax({
				url : url,
				data : data,
				method : 'GET'
			})
		}
		ajaxPost(url, data){
			return this.ajax({
				url : url,
				headers : {
					'Content-Type' : 'application/json;charset=uft-8'
				},
				data : JSON.stringify(data),
				method : 'POST'
			})
		}
		extend(target, source){
			for(let i in source){
				target[i] = source[i]
			}
			return target;
		}
		findInterval(getTarget, intv, maxCount){
			if(!intv){
				intv = 5000;
			}
			return new Promise((accept, reject)=>{
				let count = 0;
				let interval = setInterval(doIt, intv);
				doIt();
				function doIt() {
					if(maxCount && count >= maxCount){
						clearInterval(interval);
					}
					let t = null;
					try{
						t = getTarget.call();
					}catch (e) {

					}
					if(t){
						clearInterval(interval);
						accept(t);
					}
					if(maxCount) {
						count++;
					}
				}
			})

		}
	}
	root.cp = new Creeper();
})(window);
